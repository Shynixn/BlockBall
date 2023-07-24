FROM ubuntu:latest

RUN apt-get update && apt-get install python3-pip -y && apt-get install python3-setuptools -y \
    && pip3 install https://github.com/Shynixn/sphinx_rtd_theme/releases/download/C1.0/sphinx_rtd_theme.zip  \
    && pip3 install sphinx==6.2.1 && pip3 install sphinx-autobuild

WORKDIR /app

RUN apt install nodejs npm -y && npm install --global http-server

COPY source /app/source
RUN sphinx-build -b html /app/source/ /app/build/html

ENTRYPOINT ["/bin/bash"]
