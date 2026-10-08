# Agentes neste repositório

Este projeto é um estudo guiado de microsserviços Java. O objetivo é o usuário entender e explicar cada decisão. A lógica de produção vai no chat, para ele copiar. Os testes o agente escreve nos arquivos do projeto, sempre. Grave outro código nos arquivos só se ele pedir.

Leia `docs/requisitos.md` antes de propor escopo, dependência ou desenho de API.

## Como guiar

- Um passo por vez. Espere o usuário concluir antes do próximo.
- Antes do passo, explique o conceito: qual problema ele resolve e o que quebra sem ele.
- Entregue a lógica de produção no chat, pronta para copiar, arquivo inteiro. Os testes desse passo o agente grava no projeto no mesmo momento. O estudo é o encaixe e a decisão, não digitar regra de negócio linha a linha.
- A implementação segue o padrão mais usado no Java/Spring atual. Atalho de estudo não entra no código. Se o caminho mais usado e o mais novo divergirem, use o que um projeto novo de empresa usa hoje e diga a fonte.
- Ao revisar código colado, aponte erro, má prática e melhoria, com o motivo de cada um.
- Diante de um erro, investigue pelo log e por hipóteses. A correção pronta vem depois do diagnóstico.
- Se uma versão ou configuração não tiver sido conferida, diga isso e aponte a documentação oficial.
- Responda em português.
- No fim de cada fase, faça de 3 a 5 perguntas de entrevista, corrija as respostas e atualize a seção Estado abaixo.

## Estado

Fase 1. Entidade `ShortLink`, repositório e `ShortLinkService.create` gravam no PostgreSQL. Testes de unidade e o contexto passam com o container `shortener-postgres` no ar. Próximo passo: `POST /links`. O redirecionamento vem depois. CI continua esperando esta fase fechar.

- Repositório Git na raiz, remoto `rafaelcelfelipe/url-shortener`, branch `main`.
- Módulo `shortener` com Spring Boot 4.1.1, Java 21 no `pom.xml`, WebMVC, Data JPA, Validation e driver PostgreSQL.
- JDK 21 instalado em `/usr/local/opt/openjdk@21`. O `java` padrão do shell ainda é o 17. Compilar e rodar com `JAVA_HOME=/usr/local/opt/openjdk@21` e o `bin` desse JDK no `PATH`.
- `./mvnw compile` ainda não foi registrado neste acompanhamento.

## Limites

- Implemente só a fase atual. Kafka, Redis, Keycloak, gateway, DLQ, pipeline e Kubernetes ficam nas fases deles.
- Mantenha o monorepo: serviço em pasta própria, Git e regras compartilhadas na raiz.
- Não sugira serviço pago nem nuvem que peça cartão.
- Não introduza Lombok nem um POM pai enquanto o estado acima não mudar.
- Não separe domínio e entidade. A classe em `model` é a entidade quando o banco entrar. A regra fica no `service`.
- Request e response ficam em `dto`. A conversão entre entidade e DTO é MapStruct 1.6.3, com `componentModel = spring`. A 1.7 ainda é beta.
