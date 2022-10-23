# Rede social para Ongs 

O projeto possui o Objetivo de conectar pessoas que querem ajudar e entidades que precisam dessa ajuda, de forma tal que ações
podem ser publicadas tanto para divulgação de projetos que irão acontecer, como para mostrar ações que já foram desenvolvidas.

# Back-end da Rede social para Ongs

O presente repositório tem o objetivo de possuir os endpoints que farão a manipulação do banco de dados e possuirá toda a regra de negócio do projeto.

As pricipais entidades do projeto são User, Follower, Post, Ong.

# Diagrama de caso de uso

![](images/DiagramaRedeSocial.png)
# Tecnologia utilizada

O projeto usa Quarkus, o Supersônico Subatômico Java Framework.

Se você deseja aprender mais sobre Quarkus -> https://quarkus.io/ .

## Rodar aplicação em modo Dev

Você pode rodar a aplicação com o seguinte comando
```shell script
./mvnw compile quarkus:dev
```