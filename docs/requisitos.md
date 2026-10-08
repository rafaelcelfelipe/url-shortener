# Requisitos

Encurtador de URL com estatísticas de acesso. Dois serviços independentes, um repositório, tudo gratuito e executável na máquina local.

## Problema

Alguém informa uma URL longa e recebe um endereço curto. Quem abre o endereço curto vai para a URL original. O dono do link consegue ver quantas vezes ele foi acessado.

## Fronteira dos serviços

O encurtador é dono do vínculo entre o código curto e a URL original. O serviço de estatísticas é dono da contagem de acessos. Nenhum dos dois chama o outro por HTTP. O que atravessa a fronteira é um evento.

## Encurtador

- Cria um link a partir de uma URL absoluta `http` ou `https` e devolve o código curto.
- Uma requisição ao código redireciona para a URL original.
- Um código desconhecido não redireciona.
- Guarda os links em PostgreSQL.
- Publica um evento a cada redirecionamento bem-sucedido.
- Não calcula totais de acesso.

## Estatísticas

- Consome o evento de redirecionamento.
- Expõe a quantidade de acessos de um link.
- Não cria links e não redireciona.
- Processar o mesmo evento mais de uma vez não altera a contagem além do acesso real.

## Acesso

- Criar um link exige um usuário autenticado com Keycloak (OAuth2/JWT).
- Redirecionar é público.
- A consulta de estatísticas ainda não tem regra de autenticação. Isso será decidido na fase de gateway.

## Qualidades

- O redirecionamento usa cache no Redis para não consultar o banco a cada clique.
- Publicação e consumo de eventos sobrevivem a falha transitória: retry, consumidor idempotente e fila de mensagens com erro (DLQ).
- Os testes rodam no GitHub Actions, com cobertura medida pelo JaCoCo.
- O sistema sobe em Kubernetes local (kind), com métricas no Prometheus e painéis no Grafana.

## Ambiente

Java 21, Spring Boot 4.1.1, Maven Wrapper, PostgreSQL, Kafka, Redis, Keycloak, Docker Compose, JUnit 5, Mockito, Testcontainers, JaCoCo, GitHub Actions, kind, Prometheus e Grafana.

Nada pago e nenhum serviço de nuvem que exija cartão de crédito.

## Fora do previsto

Alias escolhido pelo usuário, edição da URL original, expiração do link. Entram só se forem decididos de propósito.

## Fases

1. Encurtador: criar link, redirecionar, PostgreSQL e testes.
2. Docker Compose com o serviço e o banco.
3. Evento no Kafka a cada redirecionamento.
4. Estatísticas: consumir o evento e expor a consulta.
5. Cache com Redis no redirecionamento.
6. Gateway e Keycloak. Criar link exige login. Redirecionar continua público.
7. Retry, consumidor idempotente e DLQ.
8. GitHub Actions com testes e cobertura.
9. Deploy no kind e métricas no Grafana.

Cada fase entra quando a anterior estiver compreendida. Dependência de uma fase futura não entra no código antes da hora.

## Decisões já tomadas

- Um repositório, `url-shortener`. Uma pasta por serviço: `shortener` agora, `statistics` depois. O Git fica na raiz.
- POM pai só quando o segundo serviço existir. Até lá o `pom.xml` de `shortener` usa o parent do Spring Boot.
- Pacote `com.urlshortener.shortener`. Código em inglês. Conversa e documentação em português.
- Estilo do mercado Spring: `model` (a mesma classe é a entidade JPA), `service` com a regra, `repository`, `dto` e mapper MapStruct. Sem pacote `domain` separado da tabela.
- Spring Boot 4.1.1. O starter web desta linha é `spring-boot-starter-webmvc`. Os testes vêm nos starters `*-test`, não num único `spring-boot-starter-test`.
- Java 21, a LTS que as vagas ainda pedem. Records, pattern matching e virtual threads já são estáveis nela.
- Lombok fica de fora até o código que o framework exige estar visível.
- `.gitignore` e `.gitattributes` ficam na raiz e valem para todos os serviços.
