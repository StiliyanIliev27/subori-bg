# Събори и Местни Събития в България — Трекер
## Дипломен проект по ООП и БД

---

## Идея

Десктоп приложение на Java, което централизира информацията за местни събори, фестивали и обществени събития в България — информация, която в момента е разпръсната из Facebook групи или се предава само устно. Потребителят може да разглежда, добавя и търси събития по населено място, регион, тип и дата.

---

## База от данни

### Схема (3NF)

```
regions
  - id            INT PRIMARY KEY AUTO_INCREMENT
  - name          VARCHAR(100) NOT NULL UNIQUE

settlements
  - id            INT PRIMARY KEY AUTO_INCREMENT
  - name          VARCHAR(100) NOT NULL
  - type          ENUM('село', 'град') NOT NULL
  - region_id     INT NOT NULL → FK regions(id)

categories
  - id            INT PRIMARY KEY AUTO_INCREMENT
  - name          VARCHAR(100) NOT NULL UNIQUE

organizers
  - id            INT PRIMARY KEY AUTO_INCREMENT
  - name          VARCHAR(200) NOT NULL
  - type          ENUM('Община', 'Читалище', 'Частен') NOT NULL
  - contact       VARCHAR(200)
  - settlement_id INT NOT NULL → FK settlements(id)

events
  - id            INT PRIMARY KEY AUTO_INCREMENT
  - name          VARCHAR(200) NOT NULL
  - event_date    DATE NOT NULL
  - is_recurring  BOOLEAN DEFAULT TRUE
  - description   TEXT
  - settlement_id INT NOT NULL → FK settlements(id)
  - category_id   INT NOT NULL → FK categories(id)
  - organizer_id  INT NOT NULL → FK organizers(id)
```

### Защо е 3NF
- Всеки атрибут зависи само от PK на таблицата си — няма транзитивни зависимости
- `settlements` не съхранява директно региона, а само FK към `regions`
- `events` не съхранява населено място, категория или организатор директно — само FK-та
- `organizers` не дублира информация за населеното място — само FK към `settlements`

### Релации
```
regions      (1) ──< settlements (N)
settlements  (1) ──< organizers  (N)
settlements  (1) ──< events      (N)
categories   (1) ──< events      (N)
organizers   (1) ──< events      (N)
```

---

## Функционалности

### CRUD за всяка таблица
| Таблица      | Insert | Update | Delete | Търсене (1 критерий)     |
|--------------|--------|--------|--------|--------------------------|
| regions      | ✓      | ✓      | ✓      | по име                   |
| settlements  | ✓      | ✓      | ✓      | по ime                   |
| categories   | ✓      | ✓      | ✓      | по ime                   |
| organizers   | ✓      | ✓      | ✓      | по ime / по тип          |
| events       | ✓      | ✓      | ✓      | по дата / по месец       |

> ID-та не се показват на потребителя — навсякъде се използват имена от dropdown менюта.

### Многокритериална справка
- **Регион + Месец** — всички събори в дадена област през даден месец
- **Категория + Тип населено място** — всички фестивали в села
- **Тип организатор + Регион** — всички общински прояви в дадена област

### Статистики панел
- Брой събития по регион
- Брой ежегодни vs еднократни събития
- Топ организатори по брой организирани събития

---

## Стратегия за независима разработка

### Принцип
- **Ти завършваш твоята половина изцяло** — приложението компилира и работи само с нея
- **Колегата добавя своята половина по-късно** — замества stub класовете с реални имплементации
- **Зависимостта е само в едната посока**: колегата зависи от твоите DAO-та (за dropdown-и), но ти не зависиш от неговото

### Dependency flow
```
Твоята работа (foundation)
    ↓
DatabaseConnection, Models, RegionDAO, SettlementDAO, CategoryDAO
    ↓
MainFrame (зарежда всички панели — твои реални + негови стубове)

Колегата по-късно:
    stub OrganizersPanel  →  реален OrganizersPanel  (ползва SettlementDAO — твой)
    stub EventsPanel      →  реален EventsPanel      (ползва CategoryDAO, SettlementDAO, OrganizerDAO)
    stub SearchPanel      →  реален SearchPanel      (ползва всички DAO-та)
    stub StatisticsPanel  →  реален StatisticsPanel  (ползва всички DAO-та)
```

### Stub класове (ти ги създаваш)
Ти създаваш 4 празни placeholder панела, за да може `MainFrame` да компилира:

```java
// OrganizersPanel.java
public class OrganizersPanel extends JPanel {
    public OrganizersPanel() {
        add(new JLabel("Панел Организатори — очаква се имплементация"));
    }
}

// EventsPanel.java, SearchPanel.java, StatisticsPanel.java — същото
```

Колегата просто **заменя съдържанието** на тези класове, без да пипа нищо друго.

---

## Разпределение на работата

### Човек 1 (ти) — изцяло независима работа

| Компонент               | Описание                                              |
|-------------------------|-------------------------------------------------------|
| `schema.sql`            | Цялата DB схема (всички 5 таблици), constraints, seed данни |
| `DatabaseConnection`    | Singleton JDBC връзка                                 |
| `Region.java`           | Model                                                 |
| `Settlement.java`       | Model                                                 |
| `Category.java`         | Model                                                 |
| `Organizer.java`        | Model (нужен на колегата по-късно)                    |
| `Event.java`            | Model (нужен на колегата по-късно)                    |
| `RegionDAO.java`        | CRUD + search за региони                              |
| `SettlementDAO.java`    | CRUD + search + join към Region                       |
| `CategoryDAO.java`      | CRUD + search за категории                            |
| `MainFrame.java`        | Главен прозорец с JTabbedPane                         |
| `RegionsPanel.java`     | GUI панел за региони (пълна имплементация)            |
| `SettlementsPanel.java` | GUI панел за naselja (dropdown за регион)             |
| `CategoriesPanel.java`  | GUI панел за категории (пълна имплементация)          |
| `OrganizersPanel.java`  | **Stub** — празен placeholder                         |
| `EventsPanel.java`      | **Stub** — празен placeholder                         |
| `SearchPanel.java`      | **Stub** — празен placeholder                         |
| `StatisticsPanel.java`  | **Stub** — празен placeholder                         |

### Човек 2 (колегата) — работи върху стубовете

| Компонент               | Описание                                                        |
|-------------------------|-----------------------------------------------------------------|
| `OrganizerDAO.java`     | CRUD + search + join към Settlement                             |
| `EventDAO.java`         | CRUD + search + join към Settlement, Category, Organizer        |
| `OrganizersPanel.java`  | Замества stub — пълен GUI панел за организатори                 |
| `EventsPanel.java`      | Замества stub — пълен GUI панел за събития (3 dropdown-а)      |
| `SearchPanel.java`      | Замества stub — многокритериална справка                        |
| `StatisticsPanel.java`  | Замества stub — статистики и обобщения                          |

---

## Структура на Java проекта

```
src/
├── Main.java
├── db/
│   └── DatabaseConnection.java        ← Човек 1
├── model/
│   ├── Region.java                    ← Човек 1
│   ├── Settlement.java                ← Човек 1
│   ├── Category.java                  ← Човек 1
│   ├── Organizer.java                 ← Човек 1
│   └── Event.java                     ← Човек 1
├── dao/
│   ├── RegionDAO.java                 ← Човек 1
│   ├── SettlementDAO.java             ← Човек 1
│   ├── CategoryDAO.java               ← Човек 1
│   ├── OrganizerDAO.java              ← Човек 2
│   └── EventDAO.java                  ← Човек 2
└── ui/
    ├── MainFrame.java                 ← Човек 1
    ├── RegionsPanel.java              ← Човек 1 (пълна имплементация)
    ├── SettlementsPanel.java          ← Човек 1 (пълна имплементация)
    ├── CategoriesPanel.java           ← Човек 1 (пълна имплементация)
    ├── OrganizersPanel.java           ← Човек 1 (stub) → Човек 2 (замества)
    ├── EventsPanel.java               ← Човек 1 (stub) → Човек 2 (замества)
    ├── SearchPanel.java               ← Човек 1 (stub) → Човек 2 (замества)
    └── StatisticsPanel.java           ← Човек 1 (stub) → Човек 2 (замества)

resources/
└── schema.sql                         ← Човек 1
```

---

## Технологии

| Компонент    | Технология           |
|--------------|----------------------|
| Език         | Java 17+             |
| GUI          | Java Swing           |
| БД           | MySQL                |
| JDBC драйвер | MySQL Connector/J    |
| Build tool   | Maven                |

---

## План за имплементация

### ~~Фаза 1 — База данни (Човек 1)~~ ✅ ГОТОВО
- [x] Написване на `schema.sql` (CREATE TABLE + constraints)
- [x] Добавяне на seed данни с реални български събори
- [x] Тестване в MySQL Workbench

### ~~Фаза 2 — Java скелет (Човек 1)~~ ✅ ГОТОВО
- [x] Създаване на Maven проект
- [x] Добавяне на MySQL Connector/J в `pom.xml`
- [x] `DatabaseConnection.java` (singleton)
- [x] Всички model класове (Region, Settlement, Category, Organizer, Event)

### ~~Фаза 3 — DAO слой, Човек 1~~ ✅ ГОТОВО
- [x] `RegionDAO`
- [x] `CategoryDAO`
- [x] `SettlementDAO`

### ~~Фаза 4 — GUI, Човек 1~~ ✅ ГОТОВО
- [x] `MainFrame` с JTabbedPane
- [x] `RegionsPanel`
- [x] `SettlementsPanel`
- [x] `CategoriesPanel`
- [x] Stub панели за колегата (OrganizersPanel, EventsPanel, SearchPanel, StatisticsPanel)

---

## Оставащо за Човек 2

### Фаза 5 — DAO слой
- [ ] `OrganizerDAO` — CRUD + search + JOIN към settlements
- [ ] `EventDAO` — CRUD + search + JOIN към settlements, categories, organizers

### Фаза 6 — GUI панели
- [ ] `OrganizersPanel` — замества stub, пълен CRUD + dropdown за населено място
- [ ] `EventsPanel` — замества stub, пълен CRUD + 3 dropdown-а (населено място, категория, организатор)
- [ ] `SearchPanel` — замества stub, многокритериална справка (мин. 2 критерия от различни таблици)
- [ ] `StatisticsPanel` — замества stub, визуализация на статистики

### Фаза 7 — Финализация (заедно)
- [ ] Интеграционно тестване
- [ ] Финална валидация и error handling
- [ ] Подготовка за презентация

---

## Примерни seed данни

```sql
INSERT INTO regions (name) VALUES
('Пловдивска'), ('Старозагорска'), ('Варненска'), ('Софийска'), ('Великотърновска');

INSERT INTO categories (name) VALUES
('Събор'), ('Фестивал'), ('Панаир'), ('Празник на продукт'), ('Кукерски събор');

INSERT INTO settlements (name, type, region_id) VALUES
('Калековец', 'село', 1),
('Ситово', 'село', 2),
('Елена', 'град', 5),
('Пловдив', 'град', 1),
('Стара Загора', 'град', 2);

INSERT INTO organizers (name, type, contact, settlement_id) VALUES
('Община Марица', 'Община', 'mayor@maritsa.bg', 1),
('НЧ Просвета 1895', 'Читалище', '0888123456', 2),
('Иван Петров', 'Частен', '0877654321', 3);

INSERT INTO events (name, event_date, is_recurring, description, settlement_id, category_id, organizer_id) VALUES
('Събор на село Калековец', '2025-05-24', TRUE, 'Ежегоден събор по случай 24-ти май', 1, 1, 1),
('Празник на лютеницата', '2025-09-14', TRUE, 'Фестивал на традиционната лютеница', 2, 4, 2),
('Еленски панаир', '2025-08-02', TRUE, 'Традиционен летен панаир', 3, 3, 3);
```
