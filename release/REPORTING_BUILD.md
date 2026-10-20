# Reporting configuration for the publisher's Beta

The client DSN is public configuration embedded in the distributed APK, not an
account API token. To reproduce the publisher's Beta3 payload, set this Gradle
project property before building the source:

```sh
export ORG_GRADLE_PROJECT_glitchtipDsn='https://e36a6df0590f45d784df95e1eaf633d2@app.glitchtip.com/28969'
```

Forks and development builds should use their own reporting project or omit the
property to disable remote submission. Never send test events to the publisher's
project without authorization. An empty property still permits an offline-only
engineering build; publisher artifact validation uses `--require-reporting` so a
configured public release cannot silently omit its reporting destination.

Configuration does not grant consent. Crash summaries stay off by default;
one-time compatibility reports require a user-reviewed submission and do not
change persistent crash-report consent. No behavioral analytics is enabled.
