const {Given, When, Then} = require('@cucumber/cucumber')
const assert = require("assert");
const {call, post} = require("./common");

const app_host = process.env.APP_HOST;

let body;
let responseToCheck;

Given(/^initial json$/, function (payload) {
  body = JSON.parse(payload);
});

When(/^the client send (GET|POST|PUT|DELETE) to (.*)$/,
    async function (method, url) {
      responseToCheck = await call(method, app_host + url, body)
    });

Then(/^check statusCode is (\d+)$/, function (status) {
  assert.strictEqual(responseToCheck.status, status);

});

Then(/^check response body is$/, function (payload) {
  console.log(responseToCheck.data)

  assert.deepStrictEqual(responseToCheck.data, JSON.parse(payload));
});
