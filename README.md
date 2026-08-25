# Autoplant

다 자란 작물을 수확하면 **자동으로 다시 심어주는** Paper 플러그인.
유저별 "자동 심기 횟수(티켓)"를 MongoDB에 저장해 관리한다.

> Kotlin 학습용으로 만드는 개인 프로젝트입니다. (아래 [개인 도전 과제](#개인-도전-과제) 참고)

## 동작 방식

서바이벌 모드에서 **다 자란 작물**을 부수면, 아래 조건을 모두 만족할 때 잠시 뒤 같은 자리에 작물이 다시 심어진다.

- 남은 자동 심기 횟수(티켓)가 1 이상
- 해당 작물의 씨앗을 인벤토리에 소지

재심기가 일어나면 **씨앗 1개와 티켓 1개를 소모**한다. 부착 줄기(호박·멜론)는 성장 줄기로 되돌려 심고, 코코아·줄기는 방향(facing)을 보존한다.

지원 작물은 `config.yml`에서 자유롭게 추가/제거할 수 있으며 기본값은 다음과 같다.

| 작물 블럭 | 소모 씨앗 |
|---|---|
| 밀 / 당근 / 감자 / 비트 | 각 씨앗·작물 아이템 |
| 네더 와트 | 네더 와트 |
| 호박·멜론 줄기 (부착 포함) | 호박·멜론 씨앗 |
| 코코아 | 코코아 콩 |

## 요구 사항

- Paper **1.21+**
- Java **25**
- MongoDB (횟수 저장용)

## 빌드

```bash
./gradlew shadowJar
```

`build/libs/Autoplant-<version>.jar` 가 생성된다. 이 jar를 서버의 `plugins/` 에 넣는다.

## 설정 (`config.yml`)

```yaml
mongodb:
  uri: "mongodb://localhost:27017"
  database: "autoplant"
  collection: "player_counts"
sync:
  period-ticks: 1200      # 인메모리 캐시 -> DB 주기 동기화 간격 (틱)
replant:
  delay-ticks: 20         # 수확 후 재심기까지 지연 (틱)
crops:                    # 작물 블럭 -> 소모할 씨앗
  WHEAT: WHEAT_SEEDS
  CARROTS: CARROT
  # ...
stems:                    # 부착 줄기 -> 되돌려 심을 성장 줄기
  ATTACHED_PUMPKIN_STEM: PUMPKIN_STEM
  ATTACHED_MELON_STEM: MELON_STEM
```

`crops`/`stems` 키·값은 Bukkit `Material` 이름(심어진 **블럭** 기준: `CARROTS`, `POTATOES` 등)을 사용한다.

## 명령어

기본 명령어 `/autoplant` (별칭 `/rcc`)

| 명령어 | 설명 | 권한 |
|---|---|---|
| `/autoplant` | 본인의 남은 자동 심기 횟수 확인 | 누구나 |
| `/autoplant get <player>` | 특정 플레이어의 횟수 조회 | `autoplant.admin` |
| `/autoplant add <player> <amount>` | 횟수 증감 (음수 가능) | `autoplant.admin` |
| `/autoplant set <player> <amount>` | 횟수 설정 | `autoplant.admin` |

## 권한

| 권한 | 설명 | 기본값 |
|---|---|---|
| `autoplant.admin` | 다른 플레이어의 횟수 조회/설정/추가 | op |

## 기술 스택

Kotlin 2.3 · Paper API · Koin(DI) · MongoDB · Brigadier(명령어) · Gradle Shadow

## 개인 도전 과제

Kotlin 을 배우고는 싶은데 공식문서만 보기엔 노잼이라 만들어보는 플러그인. 기능을 단계별로 나눠 직접 개발한다.

1. 작물 블럭을 캤을 때 블럭이 잠시 후 재생성되도록 하기
2. 유저별 자동 심기 횟수를 인메모리로 저장·관리하기
3. Permission Node 기반 자동 심기 추가/제거/토글 명령어 추가하기
4. 인메모리로 관리하던 횟수를 DB와 연동하기
5. RabbitMQ 등 Message Queue 를 도입해 횟수 변경 플로우 제어하기
