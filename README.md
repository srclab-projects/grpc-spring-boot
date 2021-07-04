# ![](logo.svg) Spring Boot Starter for gRPC

## Read Me:

- AsciiDoc:
  * [English](docs/README_en.adoc)
  * [简体中文](docs/README_zh.adoc)
- Markdown:
  * [English](docs/README_en.md)
  * [简体中文](docs/README_zh.md)
- HTML:
  * [English](docs/README_en.html)
  * [简体中文](docs/README_zh.html)

More see [docs/](docs/)

## Build

This project depends on [boat-spring-boot](https://github.com/srclab-projects/boat-spring-boot), you may:

```shell
# build boat-spring-boot
git clone -b master https://github.com/srclab-projects/boat-spring-boot.git

cd boat-spring-boot && gradle clean build

# build grpc-spring-boot
git clone -b master https://github.com/srclab-projects/grpc-spring-boot.git

cd grpc-spring-boot && gradle clean build
```

**Note:**

* Some properties should be configured if you want to enable publish to remote, see publish info part
  of [build.gradle](build.gradle)
* `grpc-spring-boot` need `protoc` to compile protobuf files, some architectures don't support it (such as `ARM`);

## Join

* fredsuvn@163.com
* https://github.com/srclab-projects/grpc-spring-boot
* QQ group: 566185308

## License

[Apache 2.0 license][license]

[license]: https://www.apache.org/licenses/LICENSE-2.0.html