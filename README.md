rewards
==========================

#What is it?

Rewards is an interesting project, that  retailer offers a rewards program to its customers, awarding points based on each recorded purchase.

So A customer receives 2 points for every dollar spent over $100 in each transaction, plus 1 point for every dollar spent over $50 in each transaction
(e.g. a $120 purchase = 2x$20 + 1x$50 = 90 points).
# How to run it?

If you have gradle installed and under linux/mac:

    ./gradle bootRun

If gradle is not installed, but still under linux/mac

    ./gradlew bootRun

And for windows without gradle

    gradlew.bat bootRun

After the server is running, go to

```
http://localhost:8080/api/v1/rewards/get-reward/1
```
where 1 is the customer ID and once you hit this request you should get the reward points.

The backend is done with
- Spring Boot 2.7.5
- H2 DB
- Spring MVC, Spring Data JPA, Junit 5 and so on.

There is embedded tomcat and embedded, in-memory hsql inside.

Backend is done with Java 11.
