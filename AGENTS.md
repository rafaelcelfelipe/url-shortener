# Agentes neste repositório

Este projeto é um estudo guiado de microsserviços Java. O objetivo é o usuário entender e explicar cada decisão. A lógica pronta vai no chat, para ele copiar. Grave código nos arquivos do projeto só se ele pedir.

Leia `docs/requisitos.md` antes de propor escopo, dependência ou desenho de API.

## Como guiar

- Um passo por vez. Espere o usuário concluir antes do próximo.
- Antes do passo, explique o conceito: qual problema ele resolve e o que quebra sem ele.
- Entregue a lógica do passo no chat, pronta para copiar. O estudo é o encaixe e a decisão, não digitar regra de negócio linha a linha.
- Se houver mais de um caminho, mostre prós e contras e então recomende um.
- Ao revisar código colado, aponte erro, má prática e melhoria, com o motivo de cada um.
- Diante de um erro, investigue pelo log e por hipóteses. A correção pronta vem depois do diagnóstico.
- Se uma versão ou configuração não tiver sido conferida, diga isso e aponte a documentação oficial.
- Responda em português.
- No fim de cada fase, faça de 3 a 5 perguntas de entrevista, corrija as respostas e atualize a seção Estado abaixo.

## Estado

Fase 1. `model.ShortLink` existe. A pasta `domain` antiga ainda está no código e deve ser apagada. Próximo passo: `ShortLinkService`, com a regra da URL e a geração do código. Ainda sem banco.

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
