FROM openjdk:8-alpine

ENV LEIN_VERSION=2.8.0

RUN apk --update add curl bash npm tmux ruby nginx git make musl-dev go

# setup Clojure
RUN mkdir -p /usr/local/bin && \
    curl -so /usr/local/bin/lein https://raw.githubusercontent.com/technomancy/leiningen/$LEIN_VERSION/bin/lein && \
    chmod +x /usr/local/bin/lein && \
    lein version
RUN ln -sf /usr/share/zoneinfo/Europe/Kiev /etc/localtime
WORKDIR /home/mik/Projects/file_miktau
COPY project.clj project.clj
RUN lein deps

# setup tmuxinator
RUN gem install  tmuxinator:0.12.0 rdoc:6.0.1; exit 0

# setup electron
RUN npm install -g electron@2.0.8 --unsafe-perm=true --allow-root

# setup go
ENV GOROOT /usr/lib/go
ENV GOPATH /go
ENV PATH /go/bin:$PATH
RUN mkdir -p ${GOPATH}/src ${GOPATH}/bin

# copy files
COPY . .

# build Go
RUN cp -r /home/mik/Projects/file_miktau/backend /go/src/backend
WORKDIR /go/src/backend
RUN go get .
RUN go build

WORKDIR /home/mik/Projects/file_miktau/

# setup FS detail
RUN mkdir -p /home/mik/Downloads/metator_experiments/

ENTRYPOINT [ "/bin/bash", "-l", "-c" ]

CMD ["bash"]