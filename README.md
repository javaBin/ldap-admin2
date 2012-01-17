Strategy
--------

1. Get e-mail from user
2. Generate _reset link_ and send to user and persist the _reset identifier_ and _request time_
3. When a _valid reset link_ is opened, let user specify new password


Building and running
--------------------

To build and start the project locally issue the following sbt commands

    $ sbt
    > update
    > container:start
    > ~compile


...and keep in mind: this is __WIP__!