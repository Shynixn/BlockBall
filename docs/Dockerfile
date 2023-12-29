FROM ghcr.io/shynixn/actions/sphinx-rdt-wiki-action-base:main

RUN apt-get update && apt install curl -y \
    && curl -sL deb.nodesource.com/setup_12.x | bash - \
    && apt install nodejs -y \
    && npm install --global http-server

COPY . /tmp

RUN python3 -msphinx -M html /tmp/source /tmp/site

ENTRYPOINT ["sh","-c","http-server /tmp/site/html"]
