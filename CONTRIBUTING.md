# Contributing

Thank you for your interest in contributing to this project! 🎉👍

The following is a set of guidelines for contributing to this repo:

* Use JDK 17 or newer to build the project.
* Google's Java coding conventions are used for the project. To reformat code, run:

```bash
./gradlew spotlessApply
```

* Running some of the integration tests require to
  set `OPENAI_API_KEY` or `OPENAI_ADMIN_KEY` environment variables with your
  API keys. Refer to [this](https://platform.openai.com/docs/api-reference/authentication)
  and [this](https://platform.openai.com/organization/admin-keys) for more details. No need to run
  those tests if you don't have API keys or don't want to spend your balance. Can rely on CI
  executing those instead. 😉
* If your PR modifies a request/response object, please add the changes in `TestDataUtil` and
  run the `OpenApiSpecificationValidationTest` tests to ensure
  the [spec](https://github.com/openai/openai-openapi/raw/master/openapi.yaml) is not violated.
* If your PR adds a new endpoint, please refer to the classes extending `OpenAIClient` for code
  examples. Also, please add a test case either
  in `OpenAIIntegrationTest`, `OpenAIAssistantsApiIntegrationTest`
  or `OpenAIAdminIntegrationTest` depending on the endpoint implemented.