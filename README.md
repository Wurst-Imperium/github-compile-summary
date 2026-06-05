# GitHub Compile Summary Plugin

This is a tiny Gradle plugin that writes failed Java compiler output directly to the GitHub Actions summary.

## Why

When you click on a failed GitHub Actions run, you only see the summary by default. Vanilla Gradle writes nothing useful to the summary when `compileJava` fails.

![example of useless summary when using vanilla Gradle](https://i.imgur.com/jOrh9uy.png)

If you click through to the logs, you have not only wasted a click and waited several seconds for that complex page to load, but you also then get auto-scrolled down to the very bottom of the logs where all the Gradle spam lives.

![example of log spam when using vanilla Gradle](https://i.imgur.com/jNUk0Uc.png)

Having to scroll up 1700+ lines to find the error I'm looking for is not fun.

But what if you used the Develocity Build Scan® instead?

Then you have to input your email every couple of weeks and click on a login link because the damn thing can't even set a cookie correctly. Then you click through yet another sidebar, scroll past a useless "AI-powered failure analysis", and generally jump through even more hoops to find the error you're looking for.

This sucks. I just want my compiler output with no extra steps.

Here's what the summary looks like when using this plugin:

![example of summary when using this plugin](https://i.imgur.com/nuBKMUX.png)

No extra clicks. No login codes sent to my email. No React apps taking 5+ seconds to load. No weird scrolling physics.

It's just markdown text. I can just... read it. Wow, technology!

## How to install

Add this to your `build.gradle`:

```groovy
plugins {
    id "net.wimods.github-compile-summary" version "0.1.1"
}
```

and this to your `settings.gradle`:

```groovy
pluginManagement {
    repositories {
        exclusiveContent {
            forRepository {
                maven {
                    name = 'WIMods'
                    url = 'https://maven.wimods.net/releases'
                }
            }
            filter {
                includeGroupByRegex 'net\\.wimods(\\..*)?'
            }
        }
    }
}
```

That's it.