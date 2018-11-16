# housing-classification-example-scala

[![pipeline status](https://gitlab.com/joesan/housing-classification-example-scala/badges/master/pipeline.svg)](https://gitlab.com/joesan/housing-classification-example-scala/commits/master) [![coverage report](https://gitlab.com/joesan/housing-classification-example-scala/badges/master/coverage.svg)](https://gitlab.com/joesan/housing-classification-example-scala/commits/master)

A data preparation module for the housing classification data science problem! 
This project is built using GitLab CI / CD and runs locally! Run steps:

### Set up GitLab CI / CD

The goal for me was to be able to use my Mac as a server / production machine and the whole ML pipeline
locally. I leveraged the free GitLab CI / CD with GitHub integration for this purpose. The project is 
already hosted on GitHub and all I needed was to set up a GitLab account and link it with this project.

Please refer [here](https://docs.gitlab.com/ee/ci/ci_cd_for_external_repos/github_integration.html) for the excellent documentation here on how to link your GitHub project 
with GitLab CI / CD!

Once you integrate your GitHub project with GitLab CI / CD, GitLab will automatically clone the whole project
from GitHub and is ready for triggering your build as soon as you commit your changes in GitHub!

### Set up local GitLab CI / CD runner

With GitLab, you have the option to use shared runners or a specific runner. Now, for me since I want to run the whole
pipeline locally on my Mac, I decided to disable the shared runners and use a specific one that I could install on my Mac

Have a look [here](https://docs.gitlab.com/runner/install/) on how to set up a local GitLab runner!

As with any ML project, you will have to deal with lots of files and file systems. Since in my case, I wanted to
run everything locally on my Mac, I had to edit the config.toml file (can be found under $HOME/.gitlab-runner/) and
mount a local volume to the docker runner for GitLab. So my config.toml file looks like: Have a look at the 
volumes setting in the config.toml file below!

```
[[runners]]
  name = "My-GitLab-Runner-Name"
  url = "https://gitlab.com"
  token = "XXXXXXXXXXXXXXXXXXXXXX"
  executor = "docker"
  [runners.docker]
    tls_verify = false
    image = "alpine:latest"
    privileged = false
    disable_entrypoint_overwrite = false
    oom_kill_disable = false
    disable_cache = false
    volumes = ["/path/to/local/file:/path/to/file/on/docker/container:rw"]
    shm_size = 0
  [runners.cache]
    [runners.cache.s3]
    [runners.cache.gcs]
```

The config.toml file is automatically created as soon as you configure your GitLab runner that you just installed. Have a look 
[here](https://docs.gitlab.com/runner/register/index.html) on how to configure your GitLab runner!