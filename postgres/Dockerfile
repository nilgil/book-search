FROM postgres:17

ARG BUILD_DEPS="build-essential postgresql-server-dev-17 file make git wget ca-certificates curl"

RUN apt-get update && \
    apt-get install -y --no-install-recommends ${BUILD_DEPS} && \
    update-ca-certificates && \
    \
    # mecab-ko 설치
    mkdir /mecab-src && cd /mecab-src && \
    curl -L https://bitbucket.org/eunjeon/mecab-ko/downloads/mecab-0.996-ko-0.9.2.tar.gz -o mecab.tar.gz && \
    tar -zxf mecab.tar.gz && \
    cd mecab-0.996-ko-0.9.2 && \
    ./configure && \
    make || true && \
    make check || true && \
    make install || true && \
    \
    # mecab-ko-dic 설치
    cd /mecab-src && \
    curl -L https://bitbucket.org/eunjeon/mecab-ko-dic/downloads/mecab-ko-dic-2.1.1-20180720.tar.gz -o mecab-ko-dic.tar.gz && \
    tar -zxf mecab-ko-dic.tar.gz && \
    cd mecab-ko-dic-2.1.1-20180720 && \
    ./configure && \
    ldconfig && \
    make || true && \
    make install || true && \
    \
    # textsearch-ko 설치
    cd / && \
    git config --global http.sslVerify false && \
    git clone https://github.com/i0seph/textsearch_ko.git && \
    cd textsearch_ko && \
    make USE_PGXS=1 install && \
    \
    # 정리
    apt-get purge -y --auto-remove ${BUILD_DEPS} && \
    rm -rf /mecab-src /textsearch_ko /var/lib/apt/lists/*

USER postgres
