FROM debian:latest


# setup deps (longest step)
RUN apt-get update && \
  apt-get install -y --no-install-recommends curl bash  tmux ruby nginx git make musl-dev npm\
  libgtk2.0-0 libgtk3.0 libgconf-2-4 \
  libasound2 libxtst6 libxss1 libnss3 xvfb \
  openjdk-11-jre

# setup Clojure/deps
ENV LEIN_VERSION=2.9.8
RUN mkdir -p /usr/local/bin && \
    curl -so /usr/local/bin/lein https://raw.githubusercontent.com/technomancy/leiningen/$LEIN_VERSION/bin/lein && \
    chmod +x /usr/local/bin/lein && \
    lein version
RUN ln -sf /usr/share/zoneinfo/Europe/Kiev /etc/localtime
WORKDIR /home/mik/Projects/file_miktau
COPY project.clj project.clj
RUN lein deps

# setup tmuxinator & electron
RUN gem install  tmuxinator:0.12.0 rdoc:6.0.1 &&\
    npm install -g electron@2.0.8 --unsafe-perm=true --allow-root
    
WORKDIR /home/mik/Projects/file_miktau/electron
RUN npm install electron-updater@2.23.3 electron-log@2.2.14

# setup & build Go
ENV GO_VERSION=1.17.2
ENV GO_FILE=go$GO_VERSION.linux-amd64.tar.gz
RUN curl -so /usr/local/$GO_FILE https://dl.google.com/go/$GO_FILE && tar  -C /usr/local -xzf /usr/local/$GO_FILE
ENV PATH=/usr/local/go/bin:${PATH}

ENV JAVA_OPTS -server -Xmx8g

# copy files
WORKDIR /home/mik/Projects/file_miktau/
COPY . .
RUN cp /home/mik/Projects/file_miktau/nginx.conf /etc/nginx/sites-enabled/default && service nginx restart

# build project in Go
RUN mkdir -p /go/src/backend && cp -r /home/mik/Projects/file_miktau/backend /go/src/backend
WORKDIR /go/src/backend/backend
# RUN go get .
# RUN go build

WORKDIR /home/mik/Projects/file_miktau/

# setup FS detail
RUN mkdir -p /home/mik/Downloads/metator_experiments/
RUN apt-get install -y --no-install-recommends unar
RUN unar -o /home/mik/Downloads/metator_experiments/  /home/mik/Projects/file_miktau/test_data/b62371.zip 
RUN unar -o /home/mik/Projects/file_miktau/resources/public/js/ /home/mik/Projects/file_miktau/test_data/20211218_frontend_build.zip 

ENTRYPOINT [ "/bin/bash", "-l", "-c" ]

CMD ["bash"]

