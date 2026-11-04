# Hitz syllabus · Basque for Spanish speakers (A1)

Curricular reference: *Arian A1 · Ikaslearen liburua* (student's book) and *Arian A1 ·
Lan-koadernoa* (workbook), published by Elkar. Only the unit order, communicative goals,
grammar points and lexical fields are taken from the books. No text, dialogue, exercise, word list
or image from them goes into the app: all content is written from scratch for each goal.

> Status: draft for review. Language doubts are flagged with `TODO(revisión-nativa)`.

## 1. Structure

| App level | Maps to | Count |
|---|---|---|
| Course | The whole of Arian A1 | 1 |
| Section | 3 textbook units | 4 |
| Unit (with guidebook) | Half a textbook unit | 24 |
| Level | One step on the path | 5 per unit + 1 legendary |
| Lesson | A 10–15 exercise session | 3–5 per level |

How each textbook unit is split:

- **Unit x.1** covers the textbook's *Atarian* and *A* parts (core vocabulary and first goal).
- **Unit x.2** covers parts *B* and *C* (second and third goals).
- *Erabili* and *Badakizu?* (assessment and self-assessment) become the **unit review**, the
  unit's **story** and the **legendary level**.

Levels inside each app unit, in order:

1. New-vocabulary lesson
2. Grammar-structure lesson
3. Communicative-use lesson (chat, true/false, listening)
4. Short interactive story
5. Unit review (fed by the mistakes stored for spaced repetition)
6. Legendary (optional): review with no hints and limited hearts

Each section ends with a **section review** that mixes its 6 units.

## 2. Scope decisions

- **All 12 textbook units**, grouped in 4 sections of 3 units each (owner's decision).
- **Vocabulary: 100–150 "core" words per section.** Closed sets (numbers, weekdays and months)
  are taught in full and listed separately, outside that limit, since splitting them would leave
  meaningless gaps.
- Proper nouns (people and places in the Basque Country) are not vocabulary. The validator will
  accept them when listed in `content/syllabus/proper-nouns.csv` (created in M2).
- Inflected forms (cases, verb forms) are not new words: the validator compares lemmas.
- Data source: `content/syllabus/vocabulary.csv`. The tables in this document are generated with
  `python3 tools/syllabus_vocab.py` (and checked with `--check` in CI).

<!-- counts:start -->
| Section | Core words |
|---|---|
| 1 | 136 |
| 2 | 125 |
| 3 | 118 |
| 4 | 104 |
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
**Vocabulary (25 words)**

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
| eta | y | conj. |  |

**Closed set (not counted towards the limit):** zero (cero), bat (uno), bi (dos), hiru (tres), lau (cuatro), bost (cinco), sei (seis), zazpi (siete), zortzi (ocho), bederatzi (nueve), hamar (diez)
<!-- vocab:1.2:end -->

### Textbook 2 · Non dago? → Units 2.1 and 2.2

#### Unit 2.1 · Herrian
- **Goals:** name places and services in a town or city; ask whether a place exists and where it is.
- **Grammar:** NON case (inessive: *kalean*, *Bilbon*, *bankuan*); present of EGON; demonstratives
  *hau*, *hori*, *hura* in NON (*honetan*, *horretan*, *hartan*); BA…-(R)IK? (*Badago farmaziarik
  hemen?*); adverbs *hemen*, *hor*, *han*.

<!-- vocab:2.1:start -->
**Vocabulary (25 words)**

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
| ukan | haber; tener (auxiliar) | verb |  |
<!-- vocab:3.1:end -->

#### Unit 3.2 · Nolakoa da?
- **Goals:** describe someone's appearance and personality; ask for and give personal information
  (age, marital status, children or siblings).
- **Grammar:** NOLAKOA? / NOLA?; NORK (ergative) with proper and common nouns; present of UKAN
  (*dut*, *duzu*, *du*…; colloquial *daukat* comes in unit 4); numbers 10–100 (*hogeita hamar*,
  *berrogeita bost*); ZENBAT; chained personal-information questions.

<!-- vocab:3.2:start -->
**Vocabulary (17 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
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
**Vocabulary (21 words)**

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
<!-- vocab:4.1:end -->

#### Unit 4.2 · Etxe bila
- **Goals:** ask for and give information to rent, buy or swap a home; compare two homes; list what
  must be bought to furnish a home.
- **Grammar:** BALIO IZAN (*zenbat balio du?*); NAHI IZAN (*pisu bat alokatu nahi dut*); -AGO
  comparison (*handiagoa*, *merkeagoa*); GEHIAGO / GUTXIAGO.

<!-- vocab:4.2:start -->
**Vocabulary (22 words)**

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
| mahai | mesa | noun |  |
| aulki | silla | noun |  |
| sofa | sofá | noun |  |
| armairu | armario | noun |  |
| hozkailu | frigorífico | noun |  |
| garbigailu | lavadora | noun |  |
| labe | horno | noun |  |
| dutxa | ducha | noun |  |

**Closed set (not counted towards the limit):** mila (mil), berrehun (doscientos)
<!-- vocab:4.2:end -->

### Textbook 5 · On egin → Units 5.1 and 5.2

#### Unit 5.1 · Gustuko janaria
- **Goals:** talk about food and drink; express likes and preferences.
- **Grammar:** NOR-NORI with *gustatu* (*gustatzen zait*, *gustatzen zaizkit*); NORI case with
  proper and common nouns and demonstratives (*Aneri*, *umeari*, *honi*); NAHIAGO IZAN.

<!-- vocab:5.1:start -->
**Vocabulary (25 words)**

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
| gaur | hoy | adv. |  |
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
**Vocabulary (21 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| bihar | mañana (día) | adv. |  |
| etzi | pasado mañana | adv. |  |
| datorren | próximo; que viene | adj. |  |
| aurten | este año | adv. |  |
| orain | ahora | adv. |  |
| joan | ir | verb |  |
| etorri | venir | verb |  |
| ibili | andar | verb |  |
| egin | hacer | verb |  |
| jan | comer | verb |  |
| edan | beber | verb |  |
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
**Vocabulary (20 words)**

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
<!-- vocab:8.1:end -->

#### Unit 8.2 · Medikuarengana
- **Goals:** understand and give recommendations for common health problems; describe healthy daily
  habits.
- **Grammar:** NORENGANA (*medikuarengana*, *zuregana*); NOLA? with -TA/-DA (*hautsi du* →
  *hautsita dauka*); BA- conditionals (*sukarra baduzu…*); habitual aspect (*egunero jaikitzen
  naiz*); frequency (*astean behin*, *noizean behin*, *inoiz ez*).

<!-- vocab:8.2:start -->
**Vocabulary (19 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| osasun-etxe | centro de salud | noun |  |
| botika | medicina; medicamento | noun |  |
| pilula | pastilla | noun |  |
| hitzordu | cita | noun |  |
| errezeta | receta (médica) | noun |  |
| hautsi | romper | verb |  |
| jaiki | levantarse | verb |  |
| gosaldu | desayunar | verb |  |
| dutxatu | ducharse | verb |  |
| oheratu | acostarse | verb |  |
| inoiz | alguna vez; nunca (con ez) | adv. |  |
| noizean behin | de vez en cuando | adv. |  |
| gutxitan | pocas veces | adv. |  |
| asko | mucho | adv. |  |
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
**Vocabulary (23 words)**

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
**Vocabulary (11 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| zaindu | cuidar | verb |  |
| aurreztu | ahorrar | verb |  |
| itzali | apagar | verb |  |
| piztu | encender | verb |  |
| erabili | usar | verb |  |
| bota | tirar | verb |  |
| debekatuta | prohibido | adj. |  |
| ados | de acuerdo | adv. |  |
| uste izan | creer; opinar | verb |  |
| iritzi | opinión | noun |  |
| ezin | no poder | adv. |  |
<!-- vocab:9.2:end -->

---

## Section 4 · Mundura begira (Looking out at the world)

### Textbook 10 · Hiria ala herria? → Units 10.1 and 10.2

#### Unit 10.1 · Hiria ala herria?
- **Goals:** talk about the pros and cons of living in a city or in a town.
- **Grammar:** comparatives A + B *baino* …-AGO(A)(K); *gehiago / gutxiago*; irregular forms
  (*hobe*, *gehiago*); HAIN / HAINBESTE; superlative …-(R)IK / …-EN(A)(K) (*herririk politena*).

<!-- vocab:10.1:start -->
**Vocabulary (24 words)**

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
<!-- vocab:10.1:end -->

#### Unit 10.2 · Bizitzeko lekua
- **Goals:** give opinions on the quality of life of a place; say where one likes to spend holidays.
- **Grammar:** -ELA completives (*uste dut…*, *iruditzen zait…*, *esan du…*); negative comparatives
  (*ez da … bezain*, A + *ez* + B *baino* …-ago); -T(Z)EA with *gustatu* in the past.
  `TODO(revisión-nativa)`: settle which past form of *gustatu* is taught at A1.

<!-- vocab:10.2:start -->
**Vocabulary (18 words)**

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
| on | bueno | adj. |  |
| txar | malo | adj. |  |
| hobe | mejor | adj. |  |
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
**Vocabulary (15 words)**

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
<!-- vocab:11.1:end -->

#### Unit 11.2 · Lan bila
- **Goals:** judge whether a person fits a job (training, languages, experience); talk about past
  work experience.
- **Grammar:** indirect question (EA) …-(E)N (*galdetu dit ea ingelesa dakidan*); NORENGANDIK;
  -T(Z)EN with *ikasi* and *irakatsi* (*gidatzen ikasi du*).

<!-- vocab:11.2:start -->
**Vocabulary (15 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| jakin | saber | verb |  |
| eman | dar | verb |  |
| esan | decir | verb |  |
| ekarri | traer | verb |  |
| erakutsi | enseñar (mostrar) | verb |  |
| bidali | enviar | verb |  |
| galdetu | preguntar | verb |  |
| erantzun | responder | verb |  |
| irakatsi | enseñar (instruir) | verb |  |
| esperientzia | experiencia | noun |  |
| hizkuntza | idioma | noun |  |
| ingeles | inglés | noun |  |
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
**Vocabulary (22 words)**

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
| liburu | libro | noun |  |
| marraztu | dibujar | verb |  |
| abestu | cantar | verb |  |
| jolastu | jugar | verb |  |
| noiz | cuándo | adv. |  |
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
**Vocabulary (10 words)**

| Basque | Spanish | Type | Note |
|---|---|---|---|
| txango | excursión | noun |  |
| auto | coche | noun |  |
| bizikleta | bicicleta | noun |  |
| hegazkin | avión | noun |  |
| oinez | a pie | adv. |  |
| noraino | hasta dónde | adv. |  |
| gogo | ganas | noun |  |
| asmo | intención; plan | noun |  |
| ehuneko | porcentaje; por ciento | noun |  |
| gustuko izan | gustar (tener como preferido) | verb |  |
<!-- vocab:12.2:end -->

---

## 4. Open points

- Words in `vocabulary.csv` with a `TODO(revisión-nativa)` note.
- A few words (e.g. *frutadenda*, *gidabaimen*, *hotzeria*) do not appear in the workbook text;
  the preferred batua form should be confirmed.
- In M7 every lesson is written from scratch from these goals; the validator will reject words that
  are neither in this syllabus nor in earlier units.
