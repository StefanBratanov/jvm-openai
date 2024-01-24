# Contributing

Thank you for your interest in contributing to this project! 🎉👍

The following is a set of guidelines for contributing to this repo:

* Use JDK 17 or newer to build the project.
* Google's Java coding conventions are used for the project. To reformat code, run:

```bash
./gradlew spotlessApply
```

* Running some of the tests in classes extending `OpenAIIntegrationTestBase` require to
  set `OPENAI_API_KEY` environment variable with your
  API key. Refer to
  these [instructions](https://platform.openai.com/docs/api-reference/authentication) to create one. No need to run
  those tests if you don't have an API key or don't want to spend your balance. Can rely on CI executing those instead.
  😉
* If your PR modifies a request/response object, please add the changes in `TestDataUtil` and
  run the `OpenApiSpecificationValidationTest` tests to ensure
  the [spec](https://github.com/openai/openai-openapi/raw/master/openapi.yaml) is not violated.
* If your PR adds a new endpoint, please refer to the classes extending `OpenAIClient` for code examples.