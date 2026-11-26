# Hitz syllabus · Basque for Spanish speakers (A1 and A2.1)

Curricular reference: *Arian A1 · Ikaslearen liburua* (student's book) and *Arian A1 ·
Lan-koadernoa* (workbook), and *Arian A2.1 · Ikaslearen liburua* for the A2.1 part, all published by Elkar. Only the unit order, communicative goals,
grammar points and lexical fields are taken from the books. No text, dialogue, exercise, word list
or image from them goes into the app: all content is written from scratch for each goal.

> Status: A1 units written (M7), pending native review. A2.1 mapped (sections 5–6), lessons not
> written yet. Language doubts are flagged with `TODO(revisión-nativa)`.

## 1. Structure

| App level | Maps to | Count |
|---|---|---|
| Course | Arian A1 and A2.1 | 1 |
| Section | 3 textbook units | 4 (A1) + 2 (A2.1) |
| Unit (with guidebook) | Half a textbook unit | 24 (A1) + 12 (A2.1) |
| Level | One step on the path | 5 per unit + 1 star challenge |
| Lesson | A 10–15 exercise session | 3–5 per level |

How each textbook unit is split:

- **Unit x.1** covers the textbook's *Atarian* and *A* parts (core vocabulary and first goal).
- **Unit x.2** covers parts *B* and *C* (second and third goals).
- *Erabili* and *Badakizu?* (assessment and self-assessment) become the **unit review**, the
  unit's **story** and the **star challenge**.

Levels inside each app unit, in order:

1. New-vocabulary lesson
2. Grammar-structure lesson
3. Communicative-use lesson (chat, true/false, listening)
4. Short interactive story
5. Unit review (fed by the mistakes stored for spaced repetition)
6. Star challenge (optional, beside the path): 30 of the hardest exercises, no hints, 1–3 stars

Each section ends with a **section review** that mixes its 6 units.

In the A2.1 part (units 13.1–18.2) the grammar level of a unit may start with a short *Recuerda y
amplía* lesson when the point was already taught in A1 (see Part A2.1).

## 2. Scope decisions

- **All 12 textbook units**, grouped in 4 sections of 3 units each (owner's decision).
- **Vocabulary: 100–150 "core" words per section.** Closed sets (numbers, weekdays and months)
  are taught in full and listed separately, outside that limit, since splitting them would leave
  meaningless gaps.
- **A2.1: 150–200 core words per section** (owner's decision, 2026-11-26): its texts are richer.
  Words the A2.1 book takes for granted but Hitz A1 never taught are added where first needed
  (section 5 of this document).
- Proper nouns (people and places in the Basque Country) are not vocabulary. The validator will
  accept them when listed in `content/syllabus/proper-nouns.csv` (created in M2).
- Inflected forms (cases, verb forms) are not new words: the validator compares lemmas.
- Data source: `content/syllabus/vocabulary.csv`. The tables in this document are generated with
  `python3 tools/syllabus_vocab.py` (and checked with `--check` in CI).

<!-- counts:start -->
| Section | Core words |
|---|---|
| 1 | 141 |
| 2 | 127 |
| 3 | 115 |
| 4 | 102 |
| 5 | 169 |
| 6 | 159 |
<!-- counts:end -->

## 3. Overview

| Section | Textbook unit | App units | Main grammar |
|---|---|---|---|
| 1 · Lehen urratsak | 1 Elkar ezagutzen | 1.1 Kaixo! · 1.2 Nor zara? | IZAN (NOR), NONGO, NOR/ZER/ZEIN, article -a/-ak |
| | 2 Non dago? | 2.1 Herrian · 2.2 Non dago? | NON, EGON, demonstratives, BA…-(R)IK?, postpositions of place |
| | 3 Familia eta lagunak | 3.1 Nire jendea · 3.2 Nolakoa da? | NOREN, NOREKIN, NORK + UKAN, BIZI IZAN, numbers 10–100 |
| 2 · Etxean eta kalean | 4 Zure etxea | 4.1 Etxea · 4.2 Etxe bila | EDUKI, BEHAR IZAN, BALIO IZAN, NAHI IZAN, -AGO, GEHIAGO/GUTXIAGO |
| | 5 On egin | 5.1 Gustuko janaria · 5.2 Sukaldean | NOR-NORI (gustatu), NORI, NAHIAGO IZAN, imperative without auxiliary, discourse markers |
| | 6 Erosketak | 6.1 Dendan · 6.2 Opari bat | Verb + NAHI IZAN, -EGI(A), NOLAKOA?, NORENTZAT, causal -ELAKO |
| 3 · Egunez egun | 7 Zer berri? | 7.1 Zer egin duzu? · 7.2 Zer egingo duzu? | Perfective and future aspect, NORA/NONDIK, -T(Z)EN ARI IZAN, synthetic verbs |
| | 8 Hau buruko mina! | 8.1 Buruko mina · 8.2 Medikuarengana | NONGO MINA, ZER ORDUTAN, NORENGANA, -TA/-DA, habitual aspect, BA- conditionals |
| | 9 Zaindu ingurumena | 9.1 Birziklatu · 9.2 Ingurumena zaindu | ZEREZKOA, DAITEKE/EZIN DA, imperative and indirect imperative, -(E)NEAN |
| 4 · Mundura begira | 10 Hiria ala herria? | 10.1 Hiria ala herria? · 10.2 Bizitzeko lekua | BAINO…-AGO comparatives, HAIN/HAINBESTE, superlative, -ELA completives |
| | 11 Lan eta lan | 11.1 Lanbideak · 11.2 Lan bila | JAKIN, NORAKO, NOR-NORI-NORK, NORENGANDIK, indirect question EA…-(E)N |
| | 12 Aisialdia | 12.1 Denbora librea · 12.2 Asmoak | NOIZ = NON, ZER ORDUTATIK ZER ORDUTARA, instrumental -Z, NORAINO, -T(Z)EKO GOGOA/ASMOA, past of IZAN/UKAN |
| 5 · Lagunartean (A2.1) | A2.1 · 1 Jendeartean | 13.1 Zaletasunak · 13.2 Zer iruditzen zaizu? | ERAMAN (duration), ZERI BURUZ, *komeni zait* + -T(Z)EA, potential NOR-NORK, *dirudi* / *ematen du*, -ZALE |
| | A2.1 · 2 Gogoratzen duzu? | 14.1 Txikitan · 14.2 Garai bateko herria | past of IZAN, UKAN, EGON, EDUKI and NOR-NORI, -DUN, ZEREZKOA, time markers |
| | A2.1 · 3 Berriketan | 15.1 Zertan ari zara? · 15.2 Benetan? | *aritzen* / *aritu*, imperative, -T(Z)EKO, -(E)NIK, preterite, EA…-(E)N, -T(Z)EAGATIK, NORENGATIK, exclamatives |
| 6 · Gure ingurua (A2.1) | A2.1 · 4 Mila koloretakoak gara | 16.1 Nor bezalakoa? · 16.2 Aldaketak | NOREN ANTZA, BEZALA / BEZALAKO, *nahiz eta*, -ARREN, *ordea* / *berriz*, *dirudi* + -(E)LA / -(E)NIK, GERO ETA, *saiatu* / *hasi* / *jarraitu* |
| | A2.1 · 5 Ingurukoak | 17.1 Nor da nor? · 17.2 Alde ala kontra? | relatives (NOR), synthetic verbs, *behar izaten*, imperative, -T(Z)EN *utzi*, ALDE / KONTRA |
| | A2.1 · 6 Diruaren hotsa | 18.1 Erosle onak · 18.2 Kexa bat | *gomendatu* + -T(Z)EA, fractions, percentages, frequency, degree words, BEZAIN / ADINA, BAIZIK, relatives (NORK) |

---

## Section 1 · Lehen urratsak (First steps)

### Textbook 1 · Elkar ezagutzen → Units 1.1 and 1.2

#### Unit 1.1 · Kaixo!
- **Goals:** greet and say goodbye depending on the time of day; use courtesy phrases (thanking,
  apologising, congratulating); say one's name and ask somebody else's.
- **Grammar:** present of IZAN with *ni* and *zu* (naiz, zara); questions with *nor*, *zer* and
  *nola*; answering with *bai* and *ez*.
- **Guidebook:** when to use *egun on*, *arratsalde on* and *gabon*; subject–predicate–verb order
  in *Ni Ane naiz*.

<!-- vocab:1.1:start -->
**Vocabulary (28 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| kaixo | hola | interj. |  |
| egun on | buenos días | phrase |  |
| arratsalde on | buenas tardes | phrase |  |
| gabon | buenas noches | phrase |  |
| agur | adiós | interj. |  |
| gero arte | hasta luego | phrase |  |
| bihar arte | hasta mañana | phrase |  |
| eskerrik asko | muchas gracias | phrase |  |
| mesedez | por favor | adv. |  |
| barkatu | perdón; perdona | interj. |  |
| ez horregatik | de nada | phrase |  |
| zorionak | felicidades | interj. |  |
| ondo | bien | adv. |  |
| oso | muy | adv. |  |
| bai | sí | adv. |  |
| ez | no | adv. |  |
| ni | yo | pron. |  |
| zu | tú; usted | pron. |  |
| izan | ser | verb |  |
| izen | nombre | noun |  |
| abizen | apellido | noun |  |
| nor | quién | pron. |  |
| zer | qué | pron. |  |
| zein | cuál | pron. |  |
| nola | cómo | adv. |  |
| zer moduz | qué tal | phrase |  |
| eta | y | conj. |  |
| euskara | euskera | noun | added for autonomous learners (not in Arian) |
<!-- vocab:1.1:end -->

#### Unit 1.2 · Nor zara?
- **Goals:** introduce oneself and others; say where someone is from and what they do; give simple
  personal details (phone number, address).
- **Grammar:** personal pronouns and the full present of IZAN (NOR); proper and common nouns with
  the article -a/-ak; NONGO (*bilbokoa*, *nongoa zara?*); NOR/ZER/ZEIN question words; ALA in
  questions; noun + demonstrative (*neska hau*); alphabet and pronunciation (tz, ts, tx, z, s, x).
- **Guidebook:** how to look a word up in the dictionary (without the article); numbers 0–10 to
  read out phone numbers.

<!-- vocab:1.2:start -->
**Vocabulary (22 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| hura | él; ella; aquel | pron. |  |
| gu | nosotros | pron. |  |
| zuek | vosotros | pron. |  |
| haiek | ellos; ellas | pron. |  |
| gizon | hombre | noun |  |
| emakume | mujer | noun |  |
| mutil | chico | noun |  |
| neska | chica | noun |  |
| ume | niño; niña | noun |  |
| irakasle | profesor; profesora | noun |  |
| ikasle | estudiante | noun |  |
| mediku | médico; médica | noun |  |
| erizain | enfermero; enfermera | noun |  |
| sukaldari | cocinero; cocinera | noun |  |
| ingeniari | ingeniero; ingeniera | noun |  |
| dendari | dependiente; dependienta | noun |  |
| langabetu | parado; parada | noun |  |
| telefono | teléfono | noun |  |
| zenbaki | número | noun |  |
| helbide | dirección | noun |  |
| nongo | de dónde (origen) | adv. |  |
| hitz egin | hablar | verb | added for autonomous learners (not in Arian) |

**Closed set (not counted towards the limit):** zero (cero), bat (uno), bi (dos), hiru (tres), lau (cuatro), bost (cinco), sei (seis), zazpi (siete), zortzi (ocho), bederatzi (nueve), hamar (diez)

**Function words and survival phrases (not counted towards the limit):** ere (también), baina (pero), edo (o), ez dakit (no sé), pixka bat (un poco), berriro (otra vez), poliki (despacio)
<!-- vocab:1.2:end -->

### Textbook 2 · Non dago? → Units 2.1 and 2.2

#### Unit 2.1 · Herrian
- **Goals:** name places and services in a town or city; ask whether a place exists and where it is.
- **Grammar:** NON case (inessive: *kalean*, *Bilbon*, *bankuan*); present of EGON; demonstratives
  *hau*, *hori*, *hura* in NON (*honetan*, *horretan*, *hartan*); BA…-(R)IK? (*Badago farmaziarik
  hemen?*); adverbs *hemen*, *hor*, *han*.

<!-- vocab:2.1:start -->
**Vocabulary (26 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| herri | pueblo | noun |  |
| hiri | ciudad | noun |  |
| kale | calle | noun |  |
| plaza | plaza | noun |  |
| parke | parque | noun |  |
| eliza | iglesia | noun |  |
| udaletxe | ayuntamiento | noun |  |
| banku | banco | noun |  |
| farmazia | farmacia | noun |  |
| eskola | escuela | noun |  |
| ospitale | hospital | noun |  |
| liburutegi | biblioteca | noun |  |
| jatetxe | restaurante | noun |  |
| taberna | bar | noun |  |
| hotel | hotel | noun |  |
| geltoki | estación | noun |  |
| hondartza | playa | noun |  |
| mendi | monte | noun |  |
| non | dónde | adv. |  |
| egon | estar | verb |  |
| hau | este; esta | pron. |  |
| hori | ese; esa | pron. | homograph of hori (yellow) |
| hemen | aquí | adv. |  |
| hor | ahí | adv. |  |
| han | allí | adv. |  |
| egun | día | noun | added for autonomous learners (not in Arian) |

**Function words and survival phrases (not counted towards the limit):** gaur (hoy), bihar (mañana (día)), asko (mucho), beste (otro)
<!-- vocab:2.1:end -->

#### Unit 2.2 · Non dago?
- **Goals:** locate something or someone; give and follow simple directions; describe places; talk
  about the weather.
- **Grammar:** postpositions of place with NOREN (*bankuaren ondoan*, *etxe aurrean*); directions
  (*segi zuzen*, *eskuinera*); compass points; telling the time (*ordu bata da*, *hirurak eta
  erdiak*); NOUN + ADJECTIVE (*herri polita*); weather expressions (*euria ari du*, *bero egiten
  du*).

<!-- vocab:2.2:start -->
**Vocabulary (26 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| aurrean | delante | postp. |  |
| atzean | detrás | postp. |  |
| ondoan | al lado | postp. |  |
| gainean | encima | postp. |  |
| azpian | debajo | postp. |  |
| artean | entre | postp. |  |
| gertu | cerca | adv. |  |
| urrun | lejos | adv. |  |
| eskuin | derecha | noun |  |
| ezker | izquierda | noun |  |
| zuzen | recto | adv. |  |
| ipar | norte | noun |  |
| hego | sur | noun |  |
| ekialde | este (punto cardinal) | noun |  |
| mendebalde | oeste | noun |  |
| handi | grande | adj. |  |
| txiki | pequeño | adj. |  |
| polit | bonito | adj. |  |
| eguraldi | tiempo (meteorológico) | noun |  |
| euri | lluvia | noun |  |
| elur | nieve | noun |  |
| eguzki | sol | noun |  |
| haize | viento | noun |  |
| hotz | frío | noun |  |
| bero | calor | noun |  |
| ordu | hora | noun |  |

**Function words and survival phrases (not counted towards the limit):** orain (ahora), noiz (cuándo), beti (siempre)
<!-- vocab:2.2:end -->

### Textbook 3 · Familia eta lagunak → Units 3.1 and 3.2

#### Unit 3.1 · Nire jendea
- **Goals:** talk about family and friends: who they are, where they live and who with.
- **Grammar:** NOREN (possessive genitive: *nire*, *zure*, *Aneren*; with common nouns:
  *mutilaren*); NOREN + postposition; NOREKIN (*lagunekin*, *nirekin*); BIZI IZAN (*Gasteizen bizi
  naiz*).
- **Guidebook:** *anaia/neba* and *arreba/ahizpa*: the word depends on whether the speaker is male
  or female. `TODO(revisión-nativa)`: confirm the explanation of current usage.

<!-- vocab:3.1:start -->
**Vocabulary (21 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| aita | padre | noun |  |
| ama | madre | noun |  |
| anaia | hermano (de un chico) | noun |  |
| arreba | hermana (de un chico) | noun |  |
| ahizpa | hermana (de una chica) | noun |  |
| neba | hermano (de una chica) | noun |  |
| seme | hijo | noun |  |
| alaba | hija | noun |  |
| aitona | abuelo | noun |  |
| amona | abuela | noun |  |
| senar | marido | noun |  |
| emazte | esposa | noun |  |
| guraso | padre o madre; padres (pl.) | noun |  |
| osaba | tío | noun |  |
| izeba | tía | noun |  |
| lehengusu | primo; prima | noun |  |
| lagun | amigo; amiga | noun |  |
| noren | de quién | pron. |  |
| norekin | con quién | pron. |  |
| bizi izan | vivir | verb |  |
| gaztelania | castellano (lengua) | noun | added for autonomous learners (not in Arian) |
<!-- vocab:3.1:end -->

#### Unit 3.2 · Nolakoa da?
- **Goals:** describe someone's appearance and personality; ask for and give personal information
  (age, marital status, children or siblings).
- **Grammar:** NOLAKOA? / NOLA?; NORK (ergative) with proper and common nouns; present of UKAN
  (*dut*, *duzu*, *du*…; colloquial *daukat* comes in unit 4); numbers 10–100 (*hogeita hamar*,
  *berrogeita bost*); ZENBAT; chained personal-information questions.

<!-- vocab:3.2:start -->
**Vocabulary (18 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| ukan | haber; tener (auxiliar) | verb | moved earlier: needed by this unit's grammar or goals |
| altu | alto | adj. |  |
| baxu | bajo | adj. |  |
| lodi | gordo; grueso | adj. |  |
| argal | delgado | adj. |  |
| gazte | joven | adj. |  |
| zahar | viejo; mayor | adj. |  |
| ile | pelo | noun |  |
| begi | ojo | noun |  |
| luze | largo | adj. |  |
| motz | corto | adj. |  |
| alai | alegre | adj. |  |
| jator | majo; maja | adj. |  |
| atsegin | agradable | adj. |  |
| ezkondu | casado; casarse | verb |  |
| ezkongabe | soltero; soltera | adj. |  |
| urte | año | noun |  |
| zenbat | cuánto | adv. |  |

**Closed set (not counted towards the limit):** hamaika (once), hamabi (doce), hamahiru (trece), hamalau (catorce), hamabost (quince), hamasei (dieciséis), hamazazpi (diecisiete), hemezortzi (dieciocho), hemeretzi (diecinueve), hogei (veinte), berrogei (cuarenta), hirurogei (sesenta), laurogei (ochenta), ehun (cien)
<!-- vocab:3.2:end -->

---

## Section 2 · Etxean eta kalean (At home and out and about)

### Textbook 4 · Zure etxea → Units 4.1 and 4.2

#### Unit 4.1 · Etxea
- **Goals:** name the rooms and furniture of a home; say what a home has and what it needs.
- **Grammar:** present of EDUKI (*daukat*, *dauka*…); numbers from 100 (*ehun*, *berrehun*,
  *mila*); BEHAR IZAN (*mahai bat behar dut*); plural *-ak* with UKAN (*ditut*).

<!-- vocab:4.1:start -->
**Vocabulary (22 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| etxe | casa | noun |  |
| pisu | piso | noun |  |
| solairu | planta (de un edificio) | noun |  |
| logela | dormitorio | noun |  |
| sukalde | cocina | noun |  |
| egongela | sala de estar | noun |  |
| bainugela | cuarto de baño | noun |  |
| komun | váter; aseo | noun |  |
| terraza | terraza | noun |  |
| garaje | garaje | noun |  |
| igogailu | ascensor | noun |  |
| leiho | ventana | noun |  |
| ate | puerta | noun |  |
| eskailera | escalera | noun |  |
| atari | portal | noun |  |
| eduki | tener | verb |  |
| berri | nuevo | adj. |  |
| argitsu | luminoso | adj. |  |
| ilun | oscuro | adj. |  |
| eguzkitsu | soleado | adj. |  |
| zabal | amplio; ancho | adj. |  |
| mahai | mesa | noun | moved earlier: needed by this unit's grammar or goals |

**Closed set (not counted towards the limit):** mila (mil), berrehun (doscientos)
<!-- vocab:4.1:end -->

#### Unit 4.2 · Etxe bila
- **Goals:** ask for and give information to rent, buy or swap a home; compare two homes; list what
  must be bought to furnish a home.
- **Grammar:** BALIO IZAN (*zenbat balio du?*); NAHI IZAN (*pisu bat alokatu nahi dut*); -AGO
  comparison (*handiagoa*, *merkeagoa*); GEHIAGO / GUTXIAGO.

<!-- vocab:4.2:start -->
**Vocabulary (21 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| alokatu | alquilar | verb |  |
| erosi | comprar | verb |  |
| saldu | vender | verb |  |
| trukatu | intercambiar | verb |  |
| merke | barato | adj. |  |
| garesti | caro | adj. |  |
| prezio | precio | noun |  |
| euro | euro | noun |  |
| balio izan | costar; valer | verb |  |
| behar izan | necesitar; tener que | verb |  |
| nahi izan | querer | verb |  |
| gehiago | más | adv. |  |
| gutxiago | menos | adv. |  |
| ohe | cama | noun |  |
| aulki | silla | noun |  |
| sofa | sofá | noun |  |
| armairu | armario | noun |  |
| hozkailu | frigorífico | noun |  |
| garbigailu | lavadora | noun |  |
| labe | horno | noun |  |
| dutxa | ducha | noun |  |
<!-- vocab:4.2:end -->

### Textbook 5 · On egin → Units 5.1 and 5.2

#### Unit 5.1 · Gustuko janaria
- **Goals:** talk about food and drink; express likes and preferences.
- **Grammar:** NOR-NORI with *gustatu* (*gustatzen zait*, *gustatzen zaizkit*); NORI case with
  proper and common nouns and demonstratives (*Aneri*, *umeari*, *honi*); NAHIAGO IZAN.

<!-- vocab:5.1:start -->
**Vocabulary (27 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| janari | comida | noun |  |
| edari | bebida | noun |  |
| ogi | pan | noun |  |
| ur | agua | noun |  |
| esne | leche | noun |  |
| kafe | café | noun |  |
| te | té | noun |  |
| ardo | vino | noun |  |
| garagardo | cerveza | noun |  |
| zuku | zumo | noun |  |
| haragi | carne | noun |  |
| arrain | pescado | noun |  |
| oilasko | pollo | noun |  |
| arrautza | huevo | noun |  |
| patata | patata | noun |  |
| tomate | tomate | noun |  |
| letxuga | lechuga | noun |  |
| sagar | manzana | noun |  |
| laranja | naranja | noun |  |
| gazta | queso | noun |  |
| gustatu | gustar | verb |  |
| nahiago izan | preferir | verb |  |
| gosari | desayuno | noun |  |
| bazkari | comida (del mediodía) | noun |  |
| afari | cena | noun |  |
| jan | comer | verb | moved earlier: needed by this unit's grammar or goals |
| edan | beber | verb | moved earlier: needed by this unit's grammar or goals |
<!-- vocab:5.1:end -->

#### Unit 5.2 · Sukaldean
- **Goals:** give and follow instructions for a recipe; give advice on eating healthily.
- **Grammar:** imperative without auxiliary (*moztu tomatea*); discourse markers (*lehenengo*,
  *gero*, *ondoren*, *azkenik*); time clauses (*… baino lehen*, *…-ta gero*, *… arte*); frequency
  (*egunero*, *astero*, *askotan*); BEHAR DA / BEHAR DIRA; -T(Z)EA nominalisation (*komeni da…*,
  *kaltegarria da…*); causal *eta*.

<!-- vocab:5.2:start -->
**Vocabulary (21 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| olio | aceite | noun |  |
| gatz | sal | noun |  |
| azukre | azúcar | noun |  |
| moztu | cortar | verb |  |
| egosi | cocer | verb |  |
| frijitu | freír | verb |  |
| nahastu | mezclar | verb |  |
| gehitu | añadir | verb |  |
| berotu | calentar | verb |  |
| lapiko | olla | noun |  |
| zartagin | sartén | noun |  |
| plater | plato | noun |  |
| lehenengo | primero | adv. |  |
| gero | después | adv. |  |
| ondoren | a continuación | adv. |  |
| azkenik | por último | adv. |  |
| osasuntsu | sano | adj. |  |
| kaltegarri | perjudicial | adj. |  |
| egunero | todos los días | adv. |  |
| askotan | a menudo | adv. |  |
| komeni izan | convenir | verb |  |

**Function words and survival phrases (not counted towards the limit):** astero (cada semana)
<!-- vocab:5.2:end -->

### Textbook 6 · Erosketak → Units 6.1 and 6.2

#### Unit 6.1 · Dendan
- **Goals:** do everyday shopping in shops and shopping centres: ask for a product, ask the price
  and pay.
- **Grammar:** verb + NAHI IZAN (*ogia erosi nahi dut*); shop names with *-tegi*/*-degi*;
  ZENBAT DA?

<!-- vocab:6.1:start -->
**Vocabulary (14 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| denda | tienda | noun |  |
| okindegi | panadería | noun |  |
| harategi | carnicería | noun |  |
| arrandegi | pescadería | noun |  |
| frutadenda | frutería | noun | TODO(revisión-nativa): frutadenda or fruta-denda |
| supermerkatu | supermercado | noun |  |
| liburu-denda | librería | noun |  |
| merkataritza-gune | centro comercial | noun |  |
| saltzaile | vendedor; vendedora | noun |  |
| bezero | cliente | noun |  |
| ordaindu | pagar | verb |  |
| txartel | tarjeta | noun |  |
| eskudiru | dinero en efectivo | noun |  |
| diru | dinero | noun |  |
<!-- vocab:6.1:end -->

#### Unit 6.2 · Opari bat
- **Goals:** talk about clothes: describe them, give opinions and say what is needed; choose and buy
  a present based on someone's tastes.
- **Grammar:** -EGI(A) (*handiegia*, *garestiegia*); NOLAKOA(K) DA (DIRA)?; NOLA DAUKAZU?;
  NORENTZAT (*amarentzat*, *zuretzat*); -T(Z)EA nominalisation with *gustatu* (*irakurtzea
  gustatzen zait*); causal -ELAKO (*merkea delako*); ZERGATIK?

<!-- vocab:6.2:start -->
**Vocabulary (22 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| arropa | ropa | noun |  |
| alkandora | camisa | noun |  |
| galtza | pantalón | noun |  |
| gona | falda | noun |  |
| soineko | vestido | noun |  |
| jertse | jersey | noun |  |
| beroki | abrigo | noun |  |
| kamiseta | camiseta | noun |  |
| zapata | zapato | noun |  |
| neurri | talla; medida | noun |  |
| gorri | rojo | adj. |  |
| zuri | blanco | adj. |  |
| beltz | negro | adj. |  |
| urdin | azul | adj. |  |
| berde | verde | adj. |  |
| hori (kolorea) | amarillo | adj. | homograph of hori (that) |
| gris | gris | adj. |  |
| marroi | marrón | adj. |  |
| opari | regalo | noun |  |
| urtebetetze | cumpleaños | noun |  |
| zergatik | por qué | adv. |  |
| probatu | probar(se) | verb |  |
<!-- vocab:6.2:end -->

---

## Section 3 · Egunez egun (Day by day)

### Textbook 7 · Zer berri? → Units 7.1 and 7.2

#### Unit 7.1 · Zer egin duzu?
- **Goals:** tell and ask about what has happened recently.
- **Grammar:** perfective aspect with NOR (*joan naiz*) and NOR-NORK (*ikusi dut*) auxiliaries:
  transitive and intransitive verbs; time markers (*gaur*, *atzo*, *duela bi egun*, *orain dela
  gutxi*); NORA / NONDIK with proper and common nouns; -T(Z)ERA nominalisation (*zertara?*);
  days of the week.

<!-- vocab:7.1:start -->
**Vocabulary (24 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| atzo | ayer | adv. |  |
| herenegun | anteayer | adv. |  |
| goiz | mañana (parte del día) | noun |  |
| arratsalde | tarde | noun |  |
| gau | noche | noun |  |
| aste | semana | noun |  |
| hilabete | mes | noun |  |
| duela | hace (tiempo) | adv. |  |
| joan den | pasado (la semana pasada) | adj. |  |
| iaz | el año pasado | adv. |  |
| jaio | nacer | verb |  |
| bidaiatu | viajar | verb |  |
| ikasi | aprender; estudiar | verb |  |
| lan egin | trabajar | verb |  |
| ikusi | ver | verb |  |
| irakurri | leer | verb |  |
| idatzi | escribir | verb |  |
| deitu | llamar | verb |  |
| liburu | libro | noun | moved earlier: needed by this unit's grammar or goals |
| hartu | coger; tomar | verb |  |
| bazkaldu | comer (al mediodía) | verb |  |
| afaldu | cenar | verb |  |
| entzun | escuchar | verb |  |
| galdu | perder | verb |  |

**Closed set (not counted towards the limit):** astelehen (lunes), astearte (martes), asteazken (miércoles), ostegun (jueves), ostiral (viernes), larunbat (sábado), igande (domingo)
<!-- vocab:7.1:end -->

#### Unit 7.2 · Zer egingo duzu?
- **Goals:** tell and ask about future plans; say what someone is doing right now.
- **Grammar:** future aspect -KO/-GO (*joango naiz*, *egingo dut*); markers (*bihar*, *etzi*,
  *datorren astean*); -T(Z)EN ARI IZAN (*irakurtzen ari naiz*); present of the synthetic verbs
  EGON, JOAN, ETORRI, IBILI, EDUKI (*noa*, *nator*, *nabil*); word order of compound verbs in
  questions and answers; months of the year.

<!-- vocab:7.2:start -->
**Vocabulary (17 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| etzi | pasado mañana | adv. |  |
| datorren | próximo; que viene | adj. |  |
| aurten | este año | adv. |  |
| joan | ir | verb |  |
| etorri | venir | verb |  |
| ibili | andar | verb |  |
| egin | hacer | verb |  |
| lo egin | dormir | verb |  |
| eraman | llevar | verb |  |
| sartu | entrar; meter | verb |  |
| bukatu | terminar | verb |  |
| prestatu | preparar | verb |  |
| itzuli | volver; devolver | verb |  |
| geratu | quedar(se) | verb |  |
| berandu | tarde (con retraso) | adv. |  |
| bidaia-agentzia | agencia de viajes | noun |  |
| apaindegi | peluquería | noun |  |

**Closed set (not counted towards the limit):** urtarril (enero), otsail (febrero), martxo (marzo), apiril (abril), maiatz (mayo), ekain (junio), uztail (julio), abuztu (agosto), irail (septiembre), urri (octubre), azaro (noviembre), abendu (diciembre)
<!-- vocab:7.2:end -->

### Textbook 8 · Hau buruko mina! → Units 8.1 and 8.2

#### Unit 8.1 · Buruko mina
- **Goals:** say what hurts and describe symptoms; book an appointment at the health centre.
- **Grammar:** NONGO MINA (*buruko mina daukat*); NONGO with common and proper nouns; ZER ORDUTAN?
  (*hiruretan*).

<!-- vocab:8.1:start -->
**Vocabulary (22 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| buru | cabeza | noun |  |
| belarri | oreja; oído | noun |  |
| sudur | nariz | noun |  |
| aho | boca | noun |  |
| hortz | diente | noun |  |
| lepo | cuello | noun |  |
| beso | brazo | noun |  |
| esku | mano | noun |  |
| hatz | dedo | noun |  |
| sabel | vientre; tripa | noun |  |
| bizkar | espalda | noun |  |
| hanka | pierna | noun |  |
| oin | pie | noun |  |
| belaun | rodilla | noun |  |
| min | dolor | noun |  |
| sukar | fiebre | noun |  |
| eztul | tos | noun |  |
| hotzeria | resfriado | noun |  |
| gaixo | enfermo | adj. |  |
| nekatuta | cansado | adj. |  |
| osasun-etxe | centro de salud | noun | moved earlier: needed by this unit's grammar or goals |
| hitzordu | cita | noun | moved earlier: needed by this unit's grammar or goals |
<!-- vocab:8.1:end -->

#### Unit 8.2 · Medikuarengana
- **Goals:** understand and give recommendations for common health problems; describe healthy daily
  habits.
- **Grammar:** NORENGANA (*medikuarengana*, *zuregana*); NOLA? with -TA/-DA (*hautsi du* →
  *hautsita dauka*); BA- conditionals (*sukarra baduzu…*); habitual aspect (*egunero jaikitzen
  naiz*); frequency (*astean behin*, *noizean behin*, *inoiz ez*).

<!-- vocab:8.2:start -->
**Vocabulary (16 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| botika | medicina; medicamento | noun |  |
| pilula | pastilla | noun |  |
| errezeta | receta (médica) | noun |  |
| hautsi | romper | verb |  |
| jaiki | levantarse | verb |  |
| gosaldu | desayunar | verb |  |
| dutxatu | ducharse | verb |  |
| oheratu | acostarse | verb |  |
| inoiz | alguna vez; nunca (con ez) | adv. |  |
| noizean behin | de vez en cuando | adv. |  |
| gutxitan | pocas veces | adv. |  |
| bakarrik | solo; solamente | adv. |  |
| eseri | sentarse | verb |  |
| eskatu | pedir | verb |  |
| igeri egin | nadar | verb |  |
| sendabelar | planta medicinal | noun |  |
<!-- vocab:8.2:end -->

### Textbook 9 · Zaindu ingurumena → Units 9.1 and 9.2

#### Unit 9.1 · Birziklatu
- **Goals:** give advice on looking after the environment: where each kind of waste goes and what
  each object is made of.
- **Grammar:** ZEREZKOA? (*plastikozkoa*, *beirazkoa*); DAITEKE / EZIN DA; 3rd-person imperative
  with NOR and NOR-NORK (*bota ezazu*); indirect imperative (*esan dit… -tzeko*).

<!-- vocab:9.1:start -->
**Vocabulary (26 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| ingurumen | medio ambiente | noun |  |
| natura | naturaleza | noun |  |
| zuhaitz | árbol | noun |  |
| baso | bosque | noun |  |
| itsaso | mar | noun |  |
| ibai | río | noun |  |
| kutsadura | contaminación | noun |  |
| energia | energía | noun |  |
| argi | luz | noun |  |
| birziklatu | reciclar | verb |  |
| edukiontzi | contenedor | noun |  |
| zakarrontzi | papelera; cubo de basura | noun |  |
| plastiko | plástico | noun |  |
| beira | vidrio | noun |  |
| paper | papel | noun |  |
| kartoi | cartón | noun |  |
| botila | botella | noun |  |
| poltsa | bolsa | noun |  |
| hondakin | residuo | noun |  |
| egur | madera | noun |  |
| burdina | hierro | noun |  |
| bota | tirar | verb | moved earlier: needed by this unit's grammar or goals |
| ezin | no poder | adv. | moved earlier: needed by this unit's grammar or goals |
| esan | decir | verb | moved earlier: needed by this unit's grammar or goals |
| gela | habitación; aula | noun |  |
| leku | lugar | noun |  |
<!-- vocab:9.1:end -->

#### Unit 9.2 · Ingurumena zaindu
- **Goals:** learn about measures to protect the environment and give an opinion on them; say what
  we do to look after it.
- **Grammar:** -T(Z)EA nominalisation (*garrantzitsua da…*); impersonal EZIN DA with NOR and
  NOR-NORK; DEBEKATUTA DAGO; giving opinions (*ados nago*, *ez nago ados*, *nire ustez*); time
  clauses with -(E)NEAN (*etxera iristen naizenean*).

<!-- vocab:9.2:start -->
**Vocabulary (10 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| zaindu | cuidar | verb |  |
| aurreztu | ahorrar | verb |  |
| itzali | apagar | verb |  |
| piztu | encender | verb |  |
| erabili | usar | verb |  |
| debekatuta | prohibido | adj. |  |
| ados | de acuerdo | adv. |  |
| uste izan | creer; opinar | verb |  |
| iritzi | opinión | noun |  |
| garrantzitsu | importante | adj. | added for autonomous learners (not in Arian) |
<!-- vocab:9.2:end -->

---

## Section 4 · Mundura begira (Looking out at the world)

### Textbook 10 · Hiria ala herria? → Units 10.1 and 10.2

#### Unit 10.1 · Hiria ala herria?
- **Goals:** talk about the pros and cons of living in a city or in a town.
- **Grammar:** comparatives A + B *baino* …-AGO(A)(K); *gehiago / gutxiago*; irregular forms
  (*hobe*, *gehiago*); HAIN / HAINBESTE; superlative …-(R)IK / …-EN(A)(K) (*herririk politena*).

<!-- vocab:10.1:start -->
**Vocabulary (26 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| lasai | tranquilo | adj. |  |
| zaratatsu | ruidoso | adj. |  |
| garbi | limpio | adj. |  |
| zikin | sucio | adj. |  |
| seguru | seguro | adj. |  |
| arriskutsu | peligroso | adj. |  |
| itsusi | feo | adj. |  |
| interesgarri | interesante | adj. |  |
| aspergarri | aburrido | adj. |  |
| eroso | cómodo | adj. |  |
| baino | que (comparativo) | conj. |  |
| hain | tan | adv. |  |
| hainbeste | tanto | adv. |  |
| zerbitzu | servicio | noun |  |
| garraio | transporte | noun |  |
| autobus | autobús | noun |  |
| metro | metro | noun |  |
| tren | tren | noun |  |
| kiroldegi | polideportivo | noun |  |
| zinema | cine | noun |  |
| antzoki | teatro (edificio) | noun |  |
| museo | museo | noun |  |
| igerileku | piscina | noun |  |
| aparkaleku | aparcamiento | noun |  |
| on | bueno | adj. | moved earlier: needed by this unit's grammar or goals |
| hobe | mejor | adj. | moved earlier: needed by this unit's grammar or goals |
<!-- vocab:10.1:end -->

#### Unit 10.2 · Bizitzeko lekua
- **Goals:** give opinions on the quality of life of a place; say where one likes to spend holidays.
- **Grammar:** -ELA completives (*uste dut…*, *iruditzen zait…*, *esan du…*); negative comparatives
  (*ez da … bezain*, A + *ez* + B *baino* …-ago); -T(Z)EA with *gustatu* in the past.
  `TODO(revisión-nativa)`: settle which past form of *gustatu* is taught at A1.

<!-- vocab:10.2:start -->
**Vocabulary (16 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| trafiko | tráfico | noun |  |
| zarata | ruido | noun |  |
| bizi-kalitate | calidad de vida | noun |  |
| opor | vacaciones (oporrak) | noun |  |
| bidaia | viaje | noun |  |
| kanpin | camping | noun |  |
| atzerri | extranjero | noun |  |
| jende | gente | noun |  |
| auzo | barrio | noun |  |
| txar | malo | adj. |  |
| iruditu | parecer | verb |  |
| egoki | adecuado | adj. |  |
| turista | turista | noun |  |
| sarbide | acceso | noun |  |
| pasatu | pasar | verb |  |
| primeran | estupendamente | adv. |  |
<!-- vocab:10.2:end -->

### Textbook 11 · Lan eta lan → Units 11.1 and 11.2

#### Unit 11.1 · Lanbideak
- **Goals:** ask for and give information about a job (hours, salary, contract) and about what a
  person can do.
- **Grammar:** present of JAKIN (*dakit*, *dakizu*); -T(Z)EN with *jakin* (*badakizu gidatzen?*);
  NORAKO; present of NOR-NORI-NORK in all persons (*diot*, *didazu*…).

<!-- vocab:11.1:start -->
**Vocabulary (21 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| lan | trabajo | noun |  |
| lanbide | profesión | noun |  |
| enpresa | empresa | noun |  |
| bulego | oficina | noun |  |
| lantegi | fábrica; taller | noun |  |
| nagusi | jefe; jefa | noun |  |
| lankide | compañero de trabajo | noun |  |
| langile | trabajador; trabajadora | noun |  |
| soldata | sueldo | noun |  |
| ordutegi | horario | noun |  |
| kontratu | contrato | noun |  |
| lanaldi | jornada | noun |  |
| iragarki | anuncio | noun |  |
| gidari | conductor; conductora | noun |  |
| idazkari | secretario; secretaria | noun |  |
| jakin | saber | verb | moved earlier: needed by this unit's grammar or goals |
| eman | dar | verb | moved earlier: needed by this unit's grammar or goals |
| galdetu | preguntar | verb | moved earlier: needed by this unit's grammar or goals |
| hizkuntza | idioma | noun | moved earlier: needed by this unit's grammar or goals |
| ingeles | inglés | noun | moved earlier: needed by this unit's grammar or goals |
| gidatu | conducir | verb | added for autonomous learners (not in Arian) |
<!-- vocab:11.1:end -->

#### Unit 11.2 · Lan bila
- **Goals:** judge whether a person fits a job (training, languages, experience); talk about past
  work experience.
- **Grammar:** indirect question (EA) …-(E)N (*galdetu dit ea ingelesa dakidan*); NORENGANDIK;
  -T(Z)EN with *ikasi* and *irakatsi* (*gidatzen ikasi du*).

<!-- vocab:11.2:start -->
**Vocabulary (9 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| ekarri | traer | verb |  |
| erakutsi | enseñar (mostrar) | verb |  |
| bidali | enviar | verb |  |
| erantzun | responder | verb |  |
| irakatsi | enseñar (instruir) | verb |  |
| esperientzia | experiencia | noun |  |
| gidabaimen | carné de conducir | noun | TODO(revisión-nativa): gidabaimen or gidatzeko baimen |
| ordenagailu | ordenador | noun |  |
| libre | libre | adj. |  |
<!-- vocab:11.2:end -->

### Textbook 12 · Aisialdia → Units 12.1 and 12.2

#### Unit 12.1 · Denbora librea
- **Goals:** ask for and give information about leisure activities and courses: when, where and at
  what time they take place.
- **Grammar:** NOIZ = NON (*astelehenean*, *urrian*); ZER ORDUTATIK ZER ORDUTARA; NOLA with the
  instrumental -Z (*autoz*, *trenez*); the indefinite (*mugagabea*); NORAINO.

<!-- vocab:12.1:start -->
**Vocabulary (24 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| aisialdi | tiempo libre | noun |  |
| kirol | deporte | noun |  |
| futbol | fútbol | noun |  |
| saskibaloi | baloncesto | noun |  |
| igeriketa | natación | noun |  |
| txirrindularitza | ciclismo | noun |  |
| dantza | danza; baile | noun |  |
| musika | música | noun |  |
| kontzertu | concierto | noun |  |
| antzerki | teatro (obra) | noun |  |
| film | película | noun |  |
| marraztu | dibujar | verb |  |
| abestu | cantar | verb |  |
| jolastu | jugar | verb |  |
| auto | coche | noun | moved earlier: needed by this unit's grammar or goals |
| bizikleta | bicicleta | noun | moved earlier: needed by this unit's grammar or goals |
| oinez | a pie | adv. | moved earlier: needed by this unit's grammar or goals |
| noraino | hasta dónde | adv. | moved earlier: needed by this unit's grammar or goals |
| asteburu | fin de semana | noun |  |
| jai | fiesta; día festivo | noun |  |
| ikastaro | curso | noun |  |
| argazki | foto | noun |  |
| sukaldaritza | cocina (actividad) | noun |  |
| izena eman | apuntarse; inscribirse | verb |  |
<!-- vocab:12.1:end -->

#### Unit 12.2 · Asmoak
- **Goals:** suggest leisure plans; express wishes, intentions and conditions; describe past leisure
  habits.
- **Grammar:** -T(Z)EKO GOGOA EDUKI/UKAN; BA… -T(Z)EKO GOGORIK?; -T(Z)EKO ASMOA EDUKI/UKAN;
  percentages; real condition -(T)ZEN BA…; past of IZAN and UKAN (*nintzen*, *nuen*); habitual
  aspect in the past (*joaten nintzen*).

<!-- vocab:12.2:start -->
**Vocabulary (6 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| txango | excursión | noun |  |
| hegazkin | avión | noun |  |
| gogo | ganas | noun |  |
| asmo | intención; plan | noun |  |
| ehuneko | porcentaje; por ciento | noun |  |
| gustuko izan | gustar (tener como preferido) | verb |  |
<!-- vocab:12.2:end -->

---

---

# Part A2.1

Curricular reference: *Arian A2.1 · Ikaslearen liburua* (student's book, units 1–6). Its workbook
was not available (only the answer key), so grammar is taken from the student's book boxes alone.
The same rule applies as in A1: only unit order, goals, grammar points and lexical fields are taken
from the book; every text, dialogue and exercise is written from scratch.

> Status: syllabus map for review (phase 1). No lessons are written yet.

Each grammar point is tagged:

- **New**: not taught in Hitz A1; it gets full lessons.
- **Recall and extend**: already taught in Hitz A1 (the unit is given); it gets a short
  *Recuerda y amplía* lesson, and only the new part (other persons, the past, a new use) is
  taught in full.
- **Recognition**: understood in texts, never asked for in production exercises.

## Section 5 · Lagunartean (Among friends)

### Textbook A2.1 · 1 Jendeartean → Units 13.1 and 13.2

#### Unit 13.1 · Zaletasunak
- **Goals:** introduce oneself at the start of a course (age, origin, job, how long one has been
  learning Basque and why); say what is hard or easy and what one should do about it; say what one
  talks about with friends; describe one's habits, routine and hobbies.
- **Grammar:**
  - **New:** synthetic present of ERAMAN for duration (*bi urte daramatzat euskara ikasten*,
    *zenbat denbora daramazu…?*); *zaila / erraza da* + -T(Z)EA and the superlative *zailena*;
    *komeni zait / zaizu…* + -T(Z)EA in all persons; NORI BURUZ / ZERI BURUZ (*filmari buruz*,
    *Aneri buruz*, *zeri buruz hitz egin duzue?*).
  - **Recall and extend:** NAHI IZAN with nouns and verbs (4.2, 6.1); causal *eta* and -(E)LAKO
    (5.2, 6.2); habitual present (8.2) and ZER ORDUTAN (8.1); *gustatu* + -T(Z)EA (6.2); review
    of the cases seen in A1 (NOR, NORK, NORI, NOREN, NOREKIN, NORENTZAT, NORENGANA, NON, NONGO,
    NONDIK, NORA, NOLA).
- **Guidebook:** *daramat* vs *daramatzat* (one thing or several); why the verb after *komeni* or
  *zaila da* never takes the plural; the NORI endings with proper and common nouns.

<!-- vocab:13.1:start -->
**Vocabulary (34 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| ikasturte | curso (escolar) | noun |  |
| zail | difícil | adj. |  |
| erraz | fácil | adj. |  |
| zailtasun | dificultad | noun |  |
| hitz | palabra (hitz egin: hablar) | noun |  |
| hiztegi | vocabulario; diccionario | noun |  |
| aditz | verbo | noun |  |
| ahoskatu | pronunciar | verb |  |
| ulertu | entender | verb | bridge: assumed by the A2 book; not in Hitz A1 |
| gogoratu | recordar; acordarse | verb |  |
| errepikatu | repetir | verb |  |
| aholku | consejo | noun |  |
| lagundu | ayudar | verb | bridge: assumed by the A2 book; not in Hitz A1 |
| kontatu | contar (relatar) | verb | bridge: assumed by the A2 book; not in Hitz A1 |
| pertsona | persona | noun | bridge: assumed by the A2 book; not in Hitz A1 |
| familia | familia | noun | bridge: assumed by the A2 book; not in Hitz A1 |
| biloba | nieto; nieta | noun |  |
| erretiratu | jubilado; jubilada | noun |  |
| kazetari | periodista | noun | bridge: assumed by the A2 book; not in Hitz A1 |
| gai | tema | noun |  |
| buruz | sobre; acerca de | postp. |  |
| ohitura | costumbre; hábito | noun |  |
| zaletasun | afición | noun |  |
| bizimodu | modo de vida | noun |  |
| normalean | normalmente | adv. | bridge: assumed by the A2 book; not in Hitz A1 |
| telebista | televisión | noun | bridge: assumed by the A2 book; not in Hitz A1 |
| irrati | radio | noun | bridge: assumed by the A2 book; not in Hitz A1 |
| egunkari | periódico | noun |  |
| aldizkari | revista | noun |  |
| sare sozial | red social | noun |  |
| mezu | mensaje | noun | bridge: assumed by the A2 book; not in Hitz A1 |
| jaiegun | día festivo | noun |  |
| lanpetuta | ocupado; atareado | adj. |  |
| lisatu | planchar | verb |  |

**Function words and survival phrases (not counted towards the limit):** bakoitz (cada; cada uno), guzti (todo; todos)
<!-- vocab:13.1:end -->

#### Unit 13.2 · Zer iruditzen zaizu?
- **Goals:** make proposals and give reasons to agree on something (a group trip, a neighbours'
  meeting); say what has occurred to us or what we have thought of doing; give first impressions
  of other people's character and hobbies.
- **Grammar:**
  - **New:** potential of NOR-NORK with a third-person object in all persons (*dezaket*,
    *dezakegu*, *ditzakegu*) and of NOR in the persons a proposal needs (*joan gaitezke*);
    *bururatu / pentsatu / proposatu* + -T(Z)EA (*kontzertu bat antolatzea bururatu zaigu*);
    ordinal numbers (*-garren*, *lehenengo*); IRUDI(TU) in its synthetic form (*lasaia dirudi*,
    *ahizpak dirudite*) and EMAN = "seem" (*jatorra ematen du*); the suffix -ZALE (*mendizale*,
    *irakurzale*).
  - **Recall and extend:** DAITEKE / EZIN DA (9.1) as the base of the potential; -(E)LAKO for
    reasons (6.2); *baina* for contrast (1.2).
- **Guidebook:** *dirudi* and *ematen du* mean the same here, but only *ematen du* has a full
  periphrastic paradigm; the potential with a plural object (*ditzake*).

<!-- vocab:13.2:start -->
**Vocabulary (28 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| proposamen | propuesta | noun |  |
| proposatu | proponer | verb |  |
| bururatu | ocurrirse | verb |  |
| pentsatu | pensar | verb | bridge: assumed by the A2 book; not in Hitz A1 |
| antolatu | organizar | verb |  |
| bozkatu | votar | verb |  |
| irabazi | ganar | verb |  |
| lortu | conseguir | verb |  |
| talde | grupo | noun |  |
| bilera | reunión | noun |  |
| bizilagun | vecino; vecina | noun | bridge: assumed by the A2 book; not in Hitz A1 |
| arrazoi | razón; motivo | noun |  |
| izaera | carácter; forma de ser | noun |  |
| urduri | nervioso | adj. |  |
| lotsati | tímido; vergonzoso | adj. |  |
| ausart | valiente; atrevido | adj. |  |
| beldurti | miedoso | adj. |  |
| serio | serio | adj. |  |
| arduratsu | responsable | adj. |  |
| ordenatu | ordenado | adj. | TODO(revisión-nativa): ordenatu (adj.) or txukun |
| pozik | contento | adv. | bridge: assumed by the A2 book; not in Hitz A1 |
| irudi | parecer (dirudi) | verb |  |
| ezagutu | conocer | verb | bridge: assumed by the A2 book; not in Hitz A1 |
| egia | verdad | noun | bridge: assumed by the A2 book; not in Hitz A1 |
| pisukide | compañero de piso | noun |  |
| umezain | niñero; niñera | noun |  |
| mendizale | montañero; aficionado al monte | noun |  |
| irakurzale | aficionado a la lectura | noun |  |

**Closed set (not counted towards the limit):** bigarren (segundo), hirugarren (tercero), laugarren (cuarto), bosgarren (quinto), seigarren (sexto), zazpigarren (séptimo), zortzigarren (octavo), bederatzigarren (noveno), hamargarren (décimo)

**Function words and survival phrases (not counted towards the limit):** norbait (alguien), zerbait (algo)
<!-- vocab:13.2:end -->

### Textbook A2.1 · 2 Gogoratzen duzu? → Units 14.1 and 14.2

#### Unit 14.1 · Txikitan
- **Goals:** describe what people looked like in the past; say what toys and things one had as a
  child; describe objects in the past by colour, size, pattern and material (a lost item, a
  mistaken order, a witness statement).
- **Grammar:**
  - **New:** past of UKAN with a plural object (*nituen*, *zituen*…) and *ba-* in the past
    (*banuen*, *bazenuen?*); compound adjectives of appearance (*ilehori*, *begiurdin*,
    *burusoil*); the suffix -DUN (*marraduna*, *loreduna*); the past participle with -TAKO as a
    fixed phrase (*galdutako objektuak*, recognition).
  - **Recall and extend:** past of IZAN and UKAN in the singular (12.2), now in descriptions;
    ZEREZKOA (9.1) in the past (*zilarrezkoa zen*).
- **Guidebook:** how compound adjectives are built (noun + adjective, written as one word); -DUN
  vs ZEREZKOA (what a thing *has* vs what it is *made of*).

<!-- vocab:14.1:start -->
**Vocabulary (35 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| gaztetan | de joven | adv. |  |
| txikitan | de pequeño | adv. |  |
| lehen | antes | adv. |  |
| jostailu | juguete | noun |  |
| panpina | muñeca (juguete) | noun |  |
| pilota | pelota | noun | bridge: assumed by the A2 book; not in Hitz A1 |
| bideo-joko | videojuego | noun |  |
| ilehori | rubio | adj. |  |
| ilebeltz | moreno (de pelo negro) | adj. |  |
| begiurdin | de ojos azules | adj. |  |
| burusoil | calvo | adj. |  |
| bizar | barba | noun | bridge: assumed by the A2 book; not in Hitz A1 |
| betaurreko | gafas | noun | added for autonomous learners (not in Arian) |
| sendo | fuerte; robusto | adj. |  |
| lapur | ladrón; ladrona | noun |  |
| lapurreta | robo | noun |  |
| lekuko | testigo | noun |  |
| ertzain | ertzaina | noun |  |
| jaka | chaqueta | noun |  |
| marradun | de rayas | adj. |  |
| loredun | de flores | adj. |  |
| koadrodun | de cuadros | adj. |  |
| poltsiko | bolsillo | noun |  |
| mahuka | manga | noun |  |
| estu | estrecho | adj. |  |
| larru | cuero; piel | noun |  |
| artile | lana | noun |  |
| kotoi | algodón | noun |  |
| urre | oro | noun |  |
| zilar | plata | noun |  |
| eraztun | anillo | noun |  |
| erloju | reloj | noun | bridge: assumed by the A2 book; not in Hitz A1 |
| kolore | color | noun | bridge: assumed by the A2 book; not in Hitz A1 |
| tamaina | tamaño | noun |  |
| aurkitu | encontrar | verb |  |
<!-- vocab:14.1:end -->

#### Unit 14.2 · Garai bateko herria
- **Goals:** say where one lived as a child and ask others; say what there was in one's town and
  at home in the past; talk about old schools and appliances; describe childhood games,
  holidays and tastes.
- **Grammar:**
  - **New:** past of EGON (*nengoen*, *zegoen*, *bazegoen …-rik?*) and of EDUKI (*neukan*,
    *zeuzkan*); NOR-NORI in the past (*gustatzen zitzaidan*, *zitzaizkigun*) in all persons;
    time markers by tense (*gaztetan*, *txikitan*, *umetan*, *iaz*, *datorren urtean*).
  - **Recall and extend:** BIZI IZAN (3.1) in the past (*bizi nintzen*); *gustatu* + -T(Z)EA in
    the past (10.2, now in all persons); habitual past (12.2: *joaten nintzen*); frequency
    (*gehienetan*, *sarritan*).
- **Guidebook:** *zegoen* (there was / was located) vs *zeukan* (had); the past of *gustatu*
  agrees with the thing liked, not with the person.

<!-- vocab:14.2:start -->
**Vocabulary (22 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| garai | época | noun |  |
| jaioterri | pueblo natal | noun |  |
| zubi | puente | noun |  |
| azoka | mercado; feria | noun |  |
| etxetresna | electrodoméstico; utensilio de casa | noun |  |
| arbel | pizarra | noun |  |
| maisu | maestro; maestra | noun |  |
| jolas | juego | noun |  |
| jokatu | jugar (un partido o un juego) | verb | bridge: assumed by the A2 book; not in Hitz A1 |
| korrika | corriendo | adv. | bridge: assumed by the A2 book; not in Hitz A1 |
| ipuin | cuento | noun |  |
| txapelketa | campeonato | noun |  |
| uda | verano | noun |  |
| negu | invierno | noun |  |
| udaberri | primavera | noun |  |
| udazken | otoño | noun |  |
| gozatu | disfrutar | verb |  |
| atzerritar | extranjero (persona) | noun |  |
| gustu | gusto | noun |  |
| oroitzapen | recuerdo | noun |  |
| umetan | en la infancia | adv. |  |
| hil | morir | verb | bridge: assumed by the A2 book; not in Hitz A1 |
<!-- vocab:14.2:end -->

### Textbook A2.1 · 3 Berriketan → Units 15.1 and 15.2

#### Unit 15.1 · Zertan ari zara?
- **Goals:** say what one usually does at work and what one has been doing today; pass on what
  others asked, ordered or told us (notes, messages, a neighbour's favours); tell recent news (a
  robbery, a famous visitor) with the right time markers.
- **Grammar:**
  - **New:** habitual ARI (*-t(z)en aritzen naiz*) and perfective ARI (*-t(z)en aritu naiz*);
    the three imperative forms of intransitive verbs (*etorri!*, *etor zaitezte!*, *zatozte!*)
    — the synthetic one for recognition; the completive -(E)NIK after a negative main verb (*ez
    dut uste lapurreta gertatu denik*), taught here where it is first needed; the preterite of
    any verb (*iritsi zen*, *prestatu nuen*) against the present perfect (*gaur iritsi da*).
  - **Recall and extend:** -T(Z)EN ARI IZAN (7.2); indirect imperative -T(Z)EKO (9.1), now with
    *eskatu* and *esan* in all persons; time markers with *duela* / *orain dela* (7.1), with
    *herenegun* and parts of the day; -(E)LA completives (10.2).
- **Guidebook:** *ari naiz* (right now) vs *aritzen naiz* (usually) vs *aritu naiz* (today, and
  it is over); *gaur* goes with the present perfect, *atzo* with the preterite.

<!-- vocab:15.1:start -->
**Vocabulary (27 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| garbitu | limpiar | verb |  |
| konpondu | arreglar | verb |  |
| ureztatu | regar | verb |  |
| landare | planta | noun |  |
| lorategi | jardín | noun |  |
| giltza | llave | noun |  |
| harrera | recepción | noun |  |
| zerbitzari | camarero; camarera | noun |  |
| mesede | favor | noun |  |
| postontzi | buzón | noun |  |
| zabor | basura | noun |  |
| jaitsi | bajar | verb |  |
| paseatu | pasear | verb |  |
| albiste | noticia | noun |  |
| gertatu | pasar; ocurrir | verb | bridge: assumed by the A2 book; not in Hitz A1 |
| polizia | policía | noun |  |
| atxilotu | detener | verb |  |
| bat-batean | de repente | adv. |  |
| berehala | enseguida | adv. |  |
| iritsi | llegar | verb | bridge: assumed by the A2 book; not in Hitz A1 |
| abeslari | cantante | noun |  |
| famatu | famoso | adj. |  |
| hasi | empezar | verb | bridge: assumed by the A2 book; not in Hitz A1 |
| jarri | poner | verb | bridge: assumed by the A2 book; not in Hitz A1 |
| atera | sacar; salir | verb | bridge: assumed by the A2 book; not in Hitz A1 |
| itxaron | esperar (aguardar) | verb | bridge: assumed by the A2 book; not in Hitz A1 |
| ezuste | sorpresa | noun |  |

**Function words and survival phrases (not counted towards the limit):** ezer (nada), inor (nadie), dena (todo), laster (pronto)
<!-- vocab:15.1:end -->

#### Unit 15.2 · Benetan?
- **Goals:** ask about activities and events and report the answers (an interview with a
  traveller); explain the causes of something (road accidents, bad weather, a missed plan); react
  with surprise to news, gifts, a concert or a person who has changed.
- **Grammar:**
  - **New:** causal -T(Z)EAGATIK (*telefonoz hitz egiteagatik*) and NORENGATIK with common nouns
    (*elurragatik*, *matxurengatik*); exclamative sentences: *Hau da etxea!*, *A zer-nolako…!*,
    *Zein handia den!*, *Zenbat diru duen!*, *Bai harroak zaretela!*; degree words *batere*,
    *dezente*, *nahiko*, *ikaragarri*.
  - **Recall and extend:** indirect questions (EA) …-(E)N (11.2), now in reported speech with
    the changes of person (*zenbat denbora pasatu duen galdetu diogu*); -(E)LAKO and causal *eta*
    (5.2, 6.2).
  - **Recognition:** the formal causal prefix BAIT- (*ez baitu gainditu*), read but not produced.
- **Guidebook:** what changes when a question becomes indirect (verb person, possessives); the
  exclamatives reuse the -(E)N and -(E)LA endings already known.

<!-- vocab:15.2:start -->
**Vocabulary (23 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| elkarrizketa | entrevista; conversación | noun |  |
| belaontzi | velero | noun |  |
| abentura | aventura | noun |  |
| istripu | accidente | noun |  |
| errepide | carretera | noun |  |
| seinale | señal | noun |  |
| laino | niebla | noun |  |
| logura | sueño (ganas de dormir) | noun |  |
| mugikor | móvil (teléfono) | noun | bridge: assumed by the A2 book; not in Hitz A1 |
| harritu | sorprender(se) | verb |  |
| harrigarri | asombroso | adj. |  |
| benetan | de verdad | adv. | bridge: assumed by the A2 book; not in Hitz A1 |
| agertoki | escenario | noun |  |
| itxura | aspecto | noun |  |
| aldatu | cambiar | verb | bridge: assumed by the A2 book; not in Hitz A1 |
| zahartu | envejecer | verb |  |
| zimur | arruga | noun |  |
| harro | orgulloso | adj. |  |
| zoragarri | maravilloso | adj. |  |
| ikaragarri | tremendo; muchísimo | adj. |  |
| dezente | bastante | adv. |  |
| nahiko | bastante (suficiente) | adv. |  |
| arazo | problema | noun |  |

**Function words and survival phrases (not counted towards the limit):** batere (nada (en absoluto))
<!-- vocab:15.2:end -->

## Section 6 · Gure ingurua (Around us)

### Textbook A2.1 · 4 Mila koloretakoak gara → Units 16.1 and 16.2

#### Unit 16.1 · Nor bezalakoa?
- **Goals:** say who one looks like and compare people's looks, ways and habits; express
  objections and contrasts (twins who are nothing alike, a lifestyle that contradicts itself).
- **Grammar:**
  - **New:** NOREN ANTZA EDUKI / IZAN (*amaren antza daukat*, *ez dut aitaren antzik*); BEZALA
    (how: *aitona bezala ibiltzen da*) vs BEZALAKO(A)(K) (what like: *aita bezalakoa da*);
    concessives *nahiz eta* + verb without auxiliary (*nahiz eta baxua izan*) and -ARREN (*euria egin arren*);
    contrast with *ordea* and *berriz*.
  - **Recall and extend:** *baina* (1.2) against *ordea / berriz*; NOREN (3.1) with *antza*.
- **Guidebook:** both *bezala* and *bezalako* are "como" in Spanish: ask *nola?* or *nolakoa?*;
  where *ordea* and *berriz* go in the sentence.

<!-- vocab:16.1:start -->
**Vocabulary (26 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| antz | parecido | noun |  |
| senide | pariente | noun |  |
| biki | gemelo; mellizo | noun |  |
| kizkur | rizado | adj. |  |
| antzeko | parecido; similar | adj. |  |
| desberdin | diferente | adj. | TODO(revisión-nativa): desberdin or ezberdin (the book uses ezberdin) |
| berdin | igual | adj. |  |
| begetariano | vegetariano | adj. |  |
| erretzaile | fumador; fumadora | noun |  |
| erre | fumar | verb |  |
| kirolari | deportista | noun |  |
| lotsa | vergüenza | noun |  |
| ezagun | conocido | adj. |  |
| zorte | suerte | noun |  |
| maitasun | amor | noun |  |
| maite | querido (maite izan: querer; amar) | adj. | bridge: assumed by the A2 book; not in Hitz A1 |
| osasun | salud | noun |  |
| lanpostu | puesto de trabajo | noun |  |
| azterketa | examen | noun | bridge: assumed by the A2 book; not in Hitz A1 |
| gainditu | aprobar; superar | verb |  |
| zoriontsu | feliz | adj. |  |
| dirudun | rico (que tiene dinero) | adj. |  |
| aurpegi | cara | noun |  |
| ahots | voz | noun |  |
| barre egin | reír | verb |  |
| jantzi | vestirse; ropa | verb |  |

**Function words and survival phrases (not counted towards the limit):** bezala (como (de modo)), nahiz eta (aunque), ordea (sin embargo), berriz (en cambio), arren (aunque (-arren))
<!-- vocab:16.1:end -->

#### Unit 16.2 · Aldaketak
- **Goals:** give opinions and make guesses about situations (who will look after a baby,
  rumours at work); react to surprising news; explain life changes and how one adapts; say what
  one will start, keep on or try doing.
- **Grammar:**
  - **New:** conjectures with *dirudi* and *ematen du* + -(E)LA / -(E)NIK (*berandu etorriko dela
    dirudi*, *ez du ematen urduri dagoenik*); fixed exclamations of surprise (*benetan?*, *ez da
    egia izango!*, *a zer sorpresa!*); GERO ETA …-AGO / GEHIAGO / GUTXIAGO; *saiatu / hasi /
    jarraitu* + -T(Z)EN (*kirola egiten hasi naiz*); stages of life (*haurtzaro*, *zahartzaro*).
  - **Recall and extend:** -(E)LA (10.2) and -(E)NIK (15.1) after *uste izan* and *iruditu*;
    *dirudi* and *ematen du* (13.2) with a clause.
- **Guidebook:** a negative main verb always asks for -(E)NIK; *gero eta* never goes with *oso*.

<!-- vocab:16.2:start -->
**Vocabulary (27 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| haurtzaindegi | guardería | noun |  |
| zurrumurru | rumor | noun |  |
| sinetsi | creer | verb |  |
| ziur | seguro (convencido) | adj. |  |
| seguruenik | seguramente | adv. |  |
| agian | quizás | adv. |  |
| susmo | sospecha | noun |  |
| banandu | separarse | verb |  |
| maitemindu | enamorarse | verb |  |
| haurdun | embarazada | adj. |  |
| jaioberri | recién nacido | noun |  |
| aldaketa | cambio | noun |  |
| ohitu | acostumbrarse | verb |  |
| negar egin | llorar | verb |  |
| saiatu | intentar | verb |  |
| jarraitu | seguir; continuar | verb |  |
| utzi | dejar | verb | bridge: assumed by the A2 book; not in Hitz A1 |
| bizitza | vida | noun | bridge: assumed by the A2 book; not in Hitz A1 |
| haurtzaro | infancia | noun |  |
| gaztaro | juventud | noun |  |
| helduaro | edad adulta | noun |  |
| zahartzaro | vejez | noun |  |
| lasaitu | tranquilizarse | verb |  |
| modu | manera; forma | noun |  |
| une | momento | noun |  |
| ezkontza | boda | noun |  |
| oker | equivocado; error | adj. |  |
<!-- vocab:16.2:end -->

### Textbook A2.1 · 5 Ingurukoak → Units 17.1 and 17.2

#### Unit 17.1 · Nor da nor?
- **Goals:** describe animals (what they are like, what they do); say what is happening at a
  given moment; identify and place people, things and places (who is who in a photo, which bag is
  ours, the people on a beach or at a carnival).
- **Grammar:**
  - **New:** relative clauses with -(E)N when the antecedent is NOR in the main clause (*hizketan
    ari den emakumea Bermeokoa da*) and without antecedent (*hizketan ari dena*, *hondartzan
    daudenak*); synthetic present of EKARRI (*dakart*).
  - **Recall and extend:** -T(Z)EN ARI IZAN (7.2) against synthetic verbs, which never take it
    (*mendira noa*, not *joaten ari naiz*); synthetic present of EGON, JOAN, ETORRI, IBILI, EDUKI
    (7.2), JAKIN (11.1) and ERAMAN (13.1).
- **Guidebook:** in Basque the relative clause comes *before* the noun; the article or case goes
  on the last word (*daudenak*, *daukanari*).

<!-- vocab:17.1:start -->
**Vocabulary (25 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| animalia | animal | noun | bridge: assumed by the A2 book; not in Hitz A1 |
| txakur | perro; perra | noun |  |
| katu | gato; gata | noun |  |
| behi | vaca | noun |  |
| ardi | oveja | noun |  |
| zaldi | caballo | noun |  |
| txerri | cerdo | noun |  |
| oilo | gallina | noun |  |
| ahate | pato | noun |  |
| untxi | conejo | noun |  |
| txori | pájaro | noun |  |
| hegan egin | volar | verb |  |
| buztan | cola; rabo | noun |  |
| baserri | caserío | noun |  |
| landetxe | casa rural | noun |  |
| etxe-animalia | animal doméstico | noun | TODO(revisión-nativa): etxe-animalia or etxeko animalia |
| bainujantzi | bañador | noun |  |
| itzalkin | sombrilla | noun |  |
| kapela | sombrero | noun |  |
| lore | flor | noun |  |
| inauteri | carnaval | noun |  |
| mozorro | disfraz | noun | added for autonomous learners (not in Arian) |
| zutik | de pie | adv. |  |
| eserita | sentado | adv. |  |
| oilar | gallo | noun |  |
<!-- vocab:17.1:end -->

#### Unit 17.2 · Alde ala kontra?
- **Goals:** explain how a job or task is done and what one usually has to, wants to, can or
  cannot do; give and ask for advice (looking after children, plants, a first-aid situation); say
  what others let us do or not; say what one is for or against (a new shopping centre, drinking in
  the street).
- **Grammar:**
  - **New:** habitual aspect of modal verbs (*behar izaten dut*, *nahi izaten dugu*, *ahal izaten
    da*, *ezin izaten dut*); -T(Z)EN + *lagundu* and -T(Z)EN + *utzi* with NOR-NORI-NORK (*ez
    didate sartzen utzi*); ZEREN / NOREN ALDE, AURKA, KONTRA EGON and ALDEKO / KONTRAKO + noun.
  - **Recall and extend:** imperative of NOR and NOR-NORK (9.1), now for *zu* and *zuek*, plural
    objects and the negative (*ez itzazu ekar*, *ez zaitez etor*); frequency with *-tik
    behin* (*bi urtetik behin*).
- **Guidebook:** how the imperative root is formed (*utzi → utz*, *lagundu → lagun*); *alde* and
  *kontra* take NOREN.

<!-- vocab:17.2:start -->
**Vocabulary (26 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| zeregin | tarea; quehacer | noun |  |
| atseden hartu | descansar | verb |  |
| txanda | turno | noun |  |
| beharrezko | necesario | adj. |  |
| azalpen | explicación | noun |  |
| kontuan hartu | tener en cuenta | verb |  |
| gozoki | golosina; caramelo | noun |  |
| suhiltzaile | bombero; bombera | noun |  |
| larrialdi | emergencia | noun |  |
| hazi | crecer | verb |  |
| estali | cubrir | verb |  |
| zigortu | castigar | verb |  |
| baimen | permiso | noun |  |
| eskubide | derecho | noun |  |
| alde | a favor | postp. |  |
| aurka | en contra | postp. |  |
| kontra | en contra | postp. |  |
| proiektu | proyecto | noun |  |
| hiritar | ciudadano; ciudadana | noun |  |
| erabaki | decidir; decisión | verb |  |
| edalontzi | vaso | noun |  |
| bidegorri | carril bici | noun |  |
| udaltzain | policía municipal | noun |  |
| isun | multa | noun |  |
| kalte | daño | noun |  |
| aukera | oportunidad; opción | noun |  |
<!-- vocab:17.2:end -->

### Textbook A2.1 · 6 Diruaren hotsa → Units 18.1 and 18.2

#### Unit 18.1 · Erosle onak
- **Goals:** describe kinds of shoppers and give consumer advice; report shopping habits with
  percentages, fractions and frequency.
- **Grammar:**
  - **New:** *komenigarria / gomendagarria da* + -T(Z)EA and *gomendatu* + -T(Z)EA; fractions
    (*erdia*, *herena*, *laurdena*, *hirutik bat*); percentages declined with the verb (*gazteen
    % 60k ez dute…*); frequency with *-ero* (*ostiralero*), *zenbatean behin?* and *sekula ez*.
  - **Recall and extend:** relative clauses (17.1), now also with NORK subjects inside the
    clause (*dirurik gastatzen ez duena*); *komeni* + -T(Z)EA (5.2, 13.1); percentages (12.2);
    frequency (5.2, 8.2).
- **Guidebook:** *inoiz ez* and *sekula ez* need a negative verb; how to read % aloud.

<!-- vocab:18.1:start -->
**Vocabulary (27 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| erosle | comprador; compradora | noun |  |
| kontsumo | consumo | noun |  |
| zalantza | duda | noun |  |
| eskaintza | oferta | noun |  |
| merkealdi | rebajas | noun | added for autonomous learners (not in Arian) |
| deskontu | descuento | noun |  |
| berme | garantía | noun |  |
| faktura | factura | noun |  |
| ordainagiri | recibo; tique | noun | TODO(revisión-nativa): ordainagiri or erosketa-tiket |
| akats | defecto; fallo | noun |  |
| hondatu | estropear(se) | verb |  |
| gorde | guardar | verb |  |
| gomendatu | recomendar | verb |  |
| gomendio | recomendación | noun |  |
| komenigarri | conveniente | adj. |  |
| gomendagarri | recomendable | adj. |  |
| inkesta | encuesta | noun |  |
| datu | dato | noun | bridge: assumed by the A2 book; not in Hitz A1 |
| joera | tendencia | noun |  |
| sare | red | noun |  |
| produktu | producto | noun |  |
| barazki | verdura; hortaliza | noun |  |
| ekologiko | ecológico | adj. |  |
| zuzenean | directamente | adv. |  |
| erosketa | compra | noun |  |
| gastatu | gastar | verb |  |
| gastu | gasto | noun |  |

**Closed set (not counted towards the limit):** erdi (mitad), heren (tercio), laurden (cuarto (fracción; eta laurden: y cuarto))

**Function words and survival phrases (not counted towards the limit):** sarritan (a menudo), sekula (nunca (sekula ez)), batzuetan (a veces)
<!-- vocab:18.1:end -->

#### Unit 18.2 · Kexa bat
- **Goals:** compare products and say how much one likes them; complain about a product or a
  service, apologise and explain what happened.
- **Grammar:**
  - **New:** degree words with verbs (*batere*, *gutxi*, *dezente*, *asko*, *izugarri*); BEZAIN
    for quality and ADINA for quantity, affirmative and negative (*herrietan ez dago hirietan
    adina…*); BAIZIK (*ez beltza, zuria baizik*); relative clauses when the antecedent is NORK
    in the main clause (*erosi dudan berokiak txanoa du*) and negative relatives.
  - **Recall and extend:** BAINO …-AGO, GEHIAGO / GUTXIAGO, HAIN / HAINBESTE (10.1);
    *ez da … bezain* (10.2); fixed phrases for anger and apologies (*sentitzen dut*, *barkatu*).
- **Guidebook:** the two compared terms take the same case; why *erosi dudan berokiak* takes -k.

<!-- vocab:18.2:start -->
**Vocabulary (28 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| marka | marca | noun |  |
| kalitate | calidad | noun |  |
| krema | crema | noun |  |
| xanpu | champú | noun |  |
| hortzetako pasta | pasta de dientes | noun |  |
| azal | piel | noun |  |
| izugarri | muchísimo | adv. |  |
| ezberdintasun | diferencia | noun | TODO(revisión-nativa): ezberdintasun or desberdintasun (match 16.1) |
| uharte | isla | noun |  |
| biztanle | habitante | noun |  |
| paisaia | paisaje | noun |  |
| karabana | caravana | noun |  |
| haserre | enfadado; enfado | adj. |  |
| kexatu | quejarse | verb |  |
| kexa | queja | noun |  |
| barkamen | perdón | noun |  |
| sentitu | sentir | verb |  |
| erreklamazio | reclamación | noun |  |
| kobratu | cobrar | verb |  |
| altzari | mueble | noun |  |
| apal | estante | noun |  |
| horma | pared | noun |  |
| zoru | suelo | noun |  |
| plater konbinatu | plato combinado | noun |  |
| tarta | tarta | noun |  |
| konparatu | comparar | verb |  |
| aztertu | analizar; examinar | verb |  |
| berritu | renovar | verb |  |

**Function words and survival phrases (not counted towards the limit):** baizik (sino), adina (tanto como)
<!-- vocab:18.2:end -->


## 4. Open points

- Words in `vocabulary.csv` with a `TODO(revisión-nativa)` note.
- A few words (e.g. *frutadenda*, *gidabaimen*, *hotzeria*) do not appear in the workbook text;
  the preferred batua form should be confirmed.
- In M7 every lesson is written from scratch from these goals; the validator will reject words that
  are neither in this syllabus nor in earlier units.
- A2.1: section names (*Lagunartean*, *Gure ingurua*) and a few lemmas marked in `vocabulary.csv`
  (*desberdin* / *ezberdin*, *etxe-animalia*, *ordainagiri*, *ordenatu* as an adjective) await
  native review.

## Additions beyond the textbook (for autonomous learners)

Arian is a classroom book: a teacher supplies basic words and phrases orally. Hitz has no teacher,
so a few very frequent words are taught earlier than in the book, or added. In
`content/syllabus/vocabulary.csv` they are marked "added for autonomous learners (not in Arian)"
or "moved earlier: needed by this unit's grammar or goals". Function words and survival phrases are
listed separately in each unit and do not count towards the 100–150 words per section.

| Unit | Added | Moved earlier |
|---|---|---|
| 1.1 | *euskara* (fixed phrase *Euskara ikasten ari naiz*) | |
| 1.2 | *ere*, *baina*, *edo*, *hitz egin* (fixed phrase *euskaraz hitz egiten dut*), *ez dakit*, *pixka bat*, *berriro*, *poliki* | |
| 2.1 | *egun* (on its own), *beste* | *gaur*, *bihar*, *asko* |
| 2.2 | *beti* | *orain*, *noiz* |
| 3.1 | *gaztelania* | |
| 3.2 | | *ukan* (its forms are taught here) |
| 4.1 | | *mahai*, *mila*, *berrehun* |
| 5.1 | | *jan*, *edan* |
| 5.2 | *astero* | |
| 7.1 | | *liburu* |
| 8.1 | | *osasun-etxe*, *hitzordu* |
| 9.1 | | *bota*, *ezin*, *esan* |
| 9.2 | *garrantzitsu* | |
| 10.1 | | *on*, *hobe* |
| 11.1 | *gidatu* | *jakin*, *eman*, *galdetu*, *hizkuntza*, *ingeles* |
| 12.1 | | *auto*, *bizikleta*, *oinez*, *noraino* |

Fixed phrases are taught as chunks before their grammar (as *bizi naiz* or *gustatzen zait*); their
forms are listed in `content/syllabus/forms/<unit>.csv` with the note "chunk".

**Still open**: *etxe aurrean* in the 2.2 grammar (*etxe* is 4.1; other places are used); giving an
address in 1.2 (asked, not given: *kale* is 2.1, *nire/zure* 3.1); telling the time above 10 in 2.2
(numbers 11+ are 3.2); *ezer* is not taught. Usage doubts are marked `TODO(revisión-nativa)` in
`content/source/units/*.yaml`.

## 5. A2.1 gap analysis

Written while mapping the A2.1 book against what Hitz A1 actually teaches (not against the Arian A1
book). Decisions were agreed with the owner on 2026-11-26.

### Grammar the book repeats from A1

| Point | Hitz A1 | A2.1 book | In Hitz A2.1 |
|---|---|---|---|
| Past of IZAN / UKAN | 12.2 (singular object) | 2 | 14.1: recall, plural object and *ba-* are new |
| *gustatu* in the past, -T(Z)EA | 10.2 | 2C | 14.2: recall, all persons and plural are new |
| ZEREZKOA | 9.1 | 2A | 14.1: recall in the past |
| Indirect imperative -T(Z)EKO | 9.1 | 3A | 15.1: recall, more verbs and persons |
| -(E)LAKO, causal *eta* | 5.2, 6.2 | 1, 3B | 13.1, 15.2: recall |
| Comparatives, HAIN / HAINBESTE | 10.1 | 6B | 18.2: recall; ADINA is new |
| *ez da … bezain* | 10.2 | 6B | 18.2: recall |
| EA …-(E)N | 11.2 | 3B | 15.2: recall, reported speech is new |
| -(E)LA completives | 10.2 | 3A, 4B | 15.1, 16.2: recall; -(E)NIK is new |
| Percentages | 12.2 | 6A | 18.1: recall, declension is new |
| Habitual present and past | 8.2, 12.2 | 1A, 2C | 13.1, 14.2: recall |
| Synthetic present verbs | 7.2, 11.1 | 5 | 17.1: recall; ERAMAN (13.1) and EKARRI are new |
| -T(Z)EN ARI IZAN | 7.2 | 3, 5 | 15.1, 17.1: recall; *aritzen* / *aritu* are new |
| Imperative with *ezazu* | 9.1 | 3A, 5B | 15.1, 17.2: recall, plural and negative are new |
| *komeni da* + -T(Z)EA | 5.2, 9.2 | 1, 6 | 13.1, 18.1: recall, NORI forms are new |
| Frequency, time markers | 5.2, 7.1, 8.2 | 2C, 3A, 6A | recall wherever used |

### Words the book takes for granted ("bridge" words)

The A2.1 book uses, without explaining them, basic words that the Arian A1 classroom teaches but
Hitz A1 does not (its sections were capped at 100–150 words). They are taught in the unit that
first needs them, a few per unit, and marked "bridge: assumed by the A2 book; not in Hitz A1" in
`vocabulary.csv`. Indefinites and very frequent adverbs go in the function-word lists.

| Unit | Bridge words |
|---|---|
| 13.1 | *ulertu*, *lagundu*, *kontatu*, *pertsona*, *familia*, *kazetari*, *normalean*, *telebista*, *irrati*, *mezu* (+ *bakoitz*, *guzti*) |
| 13.2 | *pentsatu*, *bizilagun*, *pozik*, *ezagutu*, *egia* (+ *norbait*, *zerbait*) |
| 14.1 | *pilota*, *bizar*, *erloju*, *kolore* |
| 14.2 | *jokatu*, *korrika*, *hil* |
| 15.1 | *gertatu*, *iritsi*, *hasi*, *jarri*, *atera*, *itxaron* (+ *ezer*, *inor*, *dena*, *laster*) |
| 15.2 | *mugikor*, *benetan*, *aldatu* |
| 16.1 | *maite*, *azterketa* |
| 16.2 | *utzi*, *bizitza* |
| 17.1 | *animalia* |
| 18.1 | *datu* |

No lesson introduces more than the usual number of new words because of them: they replace
book words of lower frequency, which stay out of the core list.

### Order inside the A2.1 book

- **-(E)NIK** is taught in 3A (15.1) and reused in 4B (16.2): no change needed.
- **Exclamatives** in 3C use -(E)N and -(E)LA, both known by then: no change.
- **Relative clauses** stay where the book has them (NOR in 5A → 17.1, NORK in 6C → 18.2).
  Units 13–16 describe people and things with adjectives, -DUN, ZEREZKOA and NOREN instead, and
  no lesson text before 17.1 may use a relative clause. Lists of known forms (`forms/u17-1.csv`)
  keep the validator honest.
- **BAIT-** (3B) is formal and rare in speech: recognition only.
- **Potential of NOR** (*gaitezke*) appears in 1B next to NOR-NORK; it is taught there only in
  the persons proposals need, as an extension of DAITEKE (9.1).
- **Synthetic imperative** (*zatozte*) appears in 3A; it is taught for recognition there and the
  periphrastic imperative is the one produced (17.2).

### Product changes this part needs (phase 2)

- Sections 5 and 6 carry a CEFR label ("A2.1") in the path; the path shows one section at a
  time and its header opens a sections overview (level, goals, progress and score; "Jump here"
  leads to the placement test).
- Score: CEFR bands. The A1 sections fill 0–29 as today; the A2.1 sections fill 30–44 (half of the
  A2 band, 30–59), so no learner loses points.
- *Recuerda y amplía* lessons for the recall points above.
- Cumulative review: a node after each textbook unit (every two app units) that mixes earlier
  units, chosen by the learner's mistakes and by how long ago each was seen. Optional and worth no
  points; added to the A1 units as well.
- Placement test and onboarding texts that say "from zero to A1".
