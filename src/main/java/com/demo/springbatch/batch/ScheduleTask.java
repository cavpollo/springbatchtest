package com.demo.springbatch.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Service
public class ScheduleTask
{
//    private JobLauncher jobLauncher;
//    private Job job;
//
//    public ScheduleTask(final JobLauncher jobLauncher, final Job job) {
//        this.jobLauncher = jobLauncher;
//        this.job = job;
//    }

    @Scheduled(cron = "*/15 * * * * ?")
    public void perform()
    {
        System.out.println("Olis");
    }

    private List<File> getFiles() {
        final File folder = new File("your/path");
        final File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            return emptyList();
        }

        return Arrays.stream(listOfFiles)
                .filter(File::isFile)
                .filter(f -> f.getName().endsWith(".csv"))
                .collect(toList());
    }
}