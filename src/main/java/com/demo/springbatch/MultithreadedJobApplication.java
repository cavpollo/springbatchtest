package com.demo.springbatch;

import com.demo.springbatch.model.WarehouseOrder;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

@EnableBatchProcessing
@SpringBootApplication
public class MultithreadedJobApplication {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	@StepScope
	public FlatFileItemReader<WarehouseOrder> reader(
			@Value("#{jobParameters['inputFlatFile']}") Resource resource) {

		return new FlatFileItemReaderBuilder<WarehouseOrder>()
				.saveState(false)
				.resource(resource)
				.delimited()
				.names(new String[] {"customerOrderReference", "transporterCode", "quantity", "deliveredAt"})
				.linesToSkip(1)
				.fieldSetMapper(fieldSet -> WarehouseOrder.builder()
						.customerOrderReference(fieldSet.readString("customerOrderReference"))
						.transporterCode(fieldSet.readString("transporterCode"))
						.quantity(fieldSet.readInt("quantity"))
						.deliveredAt(LocalDateTime.parse(fieldSet.readString("deliveredAt")))
						.build())
				.build();
	}

	@Bean
	@StepScope
	public ItemProcessor<WarehouseOrder, WarehouseOrder> processor() {
		final Validator<WarehouseOrder> validator = value -> {
			// 8 orders will match
			if (value.getTransporterCode().equals("POSTNL") && value.getQuantity() == 1) {
				throw new ValidationException("PostNL cant ship only 1 item for some superfluous reason.");
			}
		};

		final ValidatingItemProcessor<WarehouseOrder> validatingItemProcessor = new ValidatingItemProcessor<>();
		validatingItemProcessor.setValidator(validator);
		validatingItemProcessor.setFilter(true); // Comment me for fault tolerance testing

		return validatingItemProcessor;
	}

	@Bean
	@StepScope
	public JdbcBatchItemWriter<WarehouseOrder> writer(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<WarehouseOrder>()
				.dataSource(dataSource)
				.beanMapped()
				.sql("INSERT INTO warehouse_orders (customer_order_reference, transporter_code, quantity, delivered_at) " +
						"VALUES (:customerOrderReference, :transporterCode, :quantity, :deliveredAt)")
				.build();
	}

	@Bean
	public Job multithreadedJob() {
		return this.jobBuilderFactory.get("multithreadedJob")
				.start(step1())
				.build();
	}

	@Bean
	public Step step1() {
		final ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(4);
		taskExecutor.setMaxPoolSize(4);
		taskExecutor.afterPropertiesSet();

		final ItemProcessListener<WarehouseOrder, WarehouseOrder> processorListener = new ItemProcessListener<WarehouseOrder, WarehouseOrder>() {

			@Override
			public void beforeProcess(final WarehouseOrder item) {
			}

			@Override
			public void onProcessError(final WarehouseOrder item, final Exception e) {
				System.out.println("The warehouseOrder '" + item.getCustomerOrderReference() + "' failed to be processed because: " + e.getMessage());
			}

			@Override
			public void afterProcess(final WarehouseOrder item, final WarehouseOrder result) {
			}
		};

		final ItemWriteListener<WarehouseOrder> writerListener = new ItemWriteListener<WarehouseOrder>() {
			@Override
			public void beforeWrite(final List<? extends WarehouseOrder> items) {
				System.out.println("Writing " + items.size() + " items.");
			}

			@Override
			public void onWriteError(final Exception exception, final List<? extends WarehouseOrder> items) {
			}

			@Override
			public void afterWrite(final List<? extends WarehouseOrder> items) {
			}
		};

		return this.stepBuilderFactory.get("step1")
				.<WarehouseOrder, WarehouseOrder>chunk(3)
				.reader(reader(null))
				.chunk(10)
				.processor(processor())
				.writer(writer(null))
				.listener(processorListener)
				.listener(writerListener)
//				.faultTolerant()  // Uncomment me for fault tolerance testing
//				.skipPolicy((throwable, skipCount) -> throwable instanceof ValidationException && skipCount < 10) // Lower me to 4 for fault tolerance testing
				.taskExecutor(taskExecutor)
				.build();
	}

	public static void main(String[] args) {
		String [] newArgs = new String[] {"inputFlatFile=/data/warehouse_orders.csv"};

		SpringApplication.run(MultithreadedJobApplication.class, newArgs);
	}
}
