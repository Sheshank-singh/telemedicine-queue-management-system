FROM ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        ansible \
        python3 \
        curl \
        net-tools \
        sudo && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /ansible
COPY . /ansible/

CMD ["ansible-playbook", "playbook.yml"]
