# Anchoring project

Anchoring two different blockchain by event gateway

## How to use

Build (installed on `./build/install/anchoring`)

```sh
> ./build.sh
```

Listen

```sh
> ./bin/anchor -l
```

Generate address

```sh
> ./bin/anchor --keygen --pasword your_password
```

Help

```sh
> ./bin/anchor --help
```

Config

```sh
> vi ./conf/config.properties
```

## Customizing

If you want to customize it, customize any method/interface marked `@Customizable`.