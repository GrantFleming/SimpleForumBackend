# SimpleForumBackend

This application serves at the back end of Simple Forum:

    https://github.com/GrantFleming/SimpleForum
    
The web api is roughly REST-ish in nature and does not hold session data etc, however the current
implementation includes no hypermedia in the response and so it not strictly RESTful.

There are no 'private' forums or posts in the current iteration of the application so requests to
retrieve forums or posts do not require authentication.

Security
========

A client should authenticate themselves for certain operations like creating new forums and posts.

A client can register users with an email and password at an open endpoint. 'Logging In'
is accomplished by requesting a token from /auth/token using Basic authentication with the 
users details. This token should be included as a Bearer token in subsequent requests that
require authentication.

Unauthenticated users can still perform certain operations like requesting forums and posts.

Web API Overview
================

| endpoint                | method | body                                  | description                                         | authentication method |
|-------------------------|--------|---------------------------------------|-----------------------------------------------------|-----------------------|
| /user/register          | POST   | x-www-form-encoded email and password | register a new user                                 | -                     |
| /auth/token             | GET    | -                                     | get a token for authentication with the application | Basic                 |
| /api/forums             | GET    | -                                     | get all forums                                      | -                     |
| /api/forums/{id}        | GET    | -                                     | get forum with the given id                         | -                     |
| /api/forums             | POST   | application/json forum                | create  a new forum                                 | Bearer                |
| /api/posts?forumId={id} | GET    | -                                     | get all posts that belong to a specific forum       | -                     |
| /api/posts/{id}         | GET    | -                                     | get post with the given id                          | -                     |
| /api/posts              | POST   | application/json post                 | create  a new post                                  | Bearer                |
