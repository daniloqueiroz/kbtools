# About kbtools

Kbtools is (for now) a small tool to extract Kanban Metrics from
Jira.

It's tailored for some specific needs:

1. Calculates throughput and cycle time for two groups of tickets: User Stories and Tasks, SubTasks and Bugs;
1. Shows the number of bugs and highest priority tickets;
1. Skips from metrics calculations any ticket with the label _skip-metrics_.

## Metrics calculation

The cycle time is calculated as the time, in days, between _In Progress_ and _Done_ statuses.

Throughput is simply a count of tickets.

# Running the tool

## Using docker
For ease of use, this project offers a helper command, `kbtools-docker`, that allows you to build and run the
project without needing `java` on your localhost, by using docker containers.

You can use `kbtools-docker build` to compile and create a docker image. Once you build the image,
just do `kbtools-docker - --help` - this will run the new generated `kbtools` docker image passing the argument
`--help` to it.

## Using your local machine

If you do have java 8+ installed on your machine, you can run the application directly using gradle:

`./gradlew run --args='--help`

## Configuring

The application requires a few environment variables to be able to connect to jira and extract the metrics.
The `kbtools-conf.example` file list and explains each of the need environment variables.

If you are running the application using the `kbtools-docker` helper, you can make a copy of the `kbtools-conf.example`
to `kbtools-conf` and add the values there. That file is going to be fed to docker as the environment files.

If you are running the application directly on your localhost, then you need to export all the required variables before
running the application.
