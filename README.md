# charities-claims-validation-api-tests

API test suite for `charities-claims-validation` service. 

The endpoints and respective request types covered are:

| Endpoints              | Request type |
|------------------------|:------------:|
| Create Upload Tracking |     POST     |     
| Upscan Callback        |     POST     |
| Delete Single Upload   |    DELETE    |
| Delete Upload          |    DELETE    |
| Update Upload Status   |     PUT      |


## Pre-requisites
Ensure to have:
* Installed MongoDB
* Installed/configured [Service Manager 2](https://github.com/hmrc/sm2)
* Cloned the `charities-claims-validation-api-tests` project

### Services
Run the following commands to start the services locally:

* Start Mongo Docker container:

```bash
docker run --rm -d -p 27017:27017 --name mongo percona/percona-server-mongodb:6.0
```

* Start `DASS_CHARITIES_ALL` services:

```bash
sm2 --start DASS_CHARITIES_ALL
```

## Test Execution
### Local
1. Run MongoDB locally.
2. Execute tests from the cloned repo directory as follows:
* To execute only the Happy Path tests (E2E tags), run tests as follows:

```bash
./run_tests.sh <environment>
```

The tests default to the `local` environment. Argument `<environment>` must be `local`, `dev`, `qa` or `staging`.

* To execute the entire test suite, run tests as follows:

```bash
./run_tests_all.sh <environment>
```
The tests default to the `local` environment. Argument `<environment>` must be `local`, `dev`, `qa` or `staging`.

## Scalafmt

Check all project files are formatted as expected as follows:

```bash
sbt scalafmtCheckAll scalafmtCheck
```

Format `*.sbt` and `project/*.scala` files as follows:

```bash
sbt scalafmtSbt
```

Format all project files as follows:

```bash
sbt scalafmtAll
```

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
