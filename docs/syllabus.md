# Temario del curso Hitz · Euskera desde castellano (A1)

Referencia curricular: *Arian A1 · Ikaslearen liburua* y *Arian A1 · Lan-koadernoa* (Elkar).
Del libro se toman **solo** el orden de las unidades, sus objetivos comunicativos, los puntos
gramaticales y los campos léxicos. Ningún texto, diálogo, ejercicio, lista ni imagen del libro
entra en la app: todo el contenido se redacta de nuevo para cada objetivo.

> Estado: borrador para revisión. Las dudas lingüísticas van marcadas con `TODO(revisión-nativa)`.

## 1. Estructura

| Nivel de la app | Equivale a | Cantidad |
|---|---|---|
| Curso | Arian A1 completo | 1 |
| Sección | 3 unidades del libro | 4 |
| Unidad (con guía) | Media unidad del libro | 24 |
| Nivel | Paso de la ruta | 5 por unidad + 1 legendario |
| Lección | Sesión de 10–15 ejercicios | 3–5 por nivel |

Correspondencia dentro de cada unidad del libro:

- **Unidad x.1** cubre las partes *Atarian* y *A* del libro (vocabulario básico y primer objetivo).
- **Unidad x.2** cubre las partes *B* y *C* (segundo y tercer objetivo).
- *Erabili* y *Badakizu?* (evaluación y autoevaluación) se convierten en el **repaso de unidad**,
  la **historia** de la unidad y el **nivel legendario**.

Niveles de cada unidad de la app, en este orden:

1. Lección de vocabulario nuevo
2. Lección de estructura gramatical
3. Lección de uso comunicativo (chat, verdadero/falso, escuchar)
4. Historia corta interactiva
5. Repaso de unidad (con los errores guardados por repetición espaciada)
6. Legendario (opcional): repaso sin pistas y con vidas limitadas

Al final de cada sección hay un **repaso de sección** que mezcla las 6 unidades.

## 2. Decisiones de alcance

- **Las 12 unidades del libro**, repartidas en 4 secciones de 3 unidades (decisión del propietario).
- **Vocabulario: 100–150 palabras "núcleo" por sección.** Las series cerradas (números, días de la
  semana y meses) se enseñan completas y se listan aparte, sin contar en el límite: partirlas
  dejaría huecos sin sentido.
- Los nombres propios (personas y lugares de Euskal Herria) no cuentan como vocabulario y el
  validador los admite si están en `content/syllabus/proper-nouns.csv` (se crea en M2).
- Las formas flexionadas (casos, formas verbales) no cuentan como palabras nuevas: el validador
  compara por lema.
- Fuente de datos: `content/syllabus/vocabulary.csv`. Las tablas de este documento se generan con
  `python3 tools/syllabus_vocab.py` (y `--check` en CI).

<!-- counts:start -->
<!-- counts:end -->

## 3. Mapa general

| Sección | Unidad del libro | Unidades de la app | Gramática principal |
|---|---|---|---|
| 1 · Lehen urratsak | 1 Elkar ezagutzen | 1.1 Kaixo! · 1.2 Nor zara? | IZAN (NOR), NONGO, NOR/ZER/ZEIN, artículo -a/-ak |
| | 2 Non dago? | 2.1 Herrian · 2.2 Non dago? | NON, EGON, demostrativos, BA…-(R)IK?, postposiciones de lugar |
| | 3 Familia eta lagunak | 3.1 Nire jendea · 3.2 Nolakoa da? | NOREN, NOREKIN, NORK + UKAN, BIZI IZAN, números 10–100 |
| 2 · Etxean eta kalean | 4 Zure etxea | 4.1 Etxea · 4.2 Etxe bila | EDUKI, BEHAR IZAN, BALIO IZAN, NAHI IZAN, -AGO, GEHIAGO/GUTXIAGO |
| | 5 On egin | 5.1 Gustuko janaria · 5.2 Sukaldean | NOR-NORI (gustatu), NORI, NAHIAGO IZAN, imperativo sin auxiliar, marcadores del discurso |
| | 6 Erosketak | 6.1 Dendan · 6.2 Opari bat | Verbo + NAHI IZAN, -EGI(A), NOLAKOA?, NORENTZAT, causales -ELAKO |
| 3 · Egunez egun | 7 Zer berri? | 7.1 Zer egin duzu? · 7.2 Zer egingo duzu? | Aspecto perfectivo y futuro, NORA/NONDIK, -T(Z)EN ARI IZAN, verbos sintéticos |
| | 8 Hau buruko mina! | 8.1 Buruko mina · 8.2 Medikuarengana | NONGO MINA, ZER ORDUTAN, NORENGANA, -TA/-DA, aspecto habitual, condicionales BA- |
| | 9 Zaindu ingurumena | 9.1 Birziklatu · 9.2 Ingurumena zaindu | ZEREZKOA, DAITEKE/EZIN DA, imperativo e imperativo indirecto, -(E)NEAN |
| 4 · Mundura begira | 10 Hiria ala herria? | 10.1 Hiria ala herria? · 10.2 Bizitzeko lekua | Comparativas BAINO…-AGO, HAIN/HAINBESTE, superlativo, completivas -ELA |
| | 11 Lan eta lan | 11.1 Lanbideak · 11.2 Lan bila | JAKIN, NORAKO, NOR-NORI-NORK, NORENGANDIK, pregunta indirecta EA…-(E)N |
| | 12 Aisialdia | 12.1 Denbora librea · 12.2 Asmoak | NOIZ = NON, ZER ORDUTATIK ZER ORDUTARA, instrumental -Z, NORAINO, -T(Z)EKO GOGOA/ASMOA, pasado de IZAN/UKAN |

---

## Sección 1 · Lehen urratsak (Primeros pasos)

### Libro 1 · Elkar ezagutzen → Unidades 1.1 y 1.2

#### Unidad 1.1 · Kaixo!
- **Objetivos:** saludar y despedirse según el momento del día; usar fórmulas de cortesía
  (agradecer, pedir perdón, felicitar); decir el propio nombre y preguntar el de otra persona.
- **Gramática:** IZAN en presente con *ni* y *zu* (naiz, zara); preguntas con *nor*, *zer* y
  *nola*; respuestas con *bai* y *ez*.
- **Guía de la unidad:** cuándo usar *egun on*, *arratsalde on* y *gabon*; el orden
  sujeto–predicado–verbo en *Ni Ane naiz*.

<!-- vocab:1.1:start -->
**Vocabulario (25 palabras)**

| Euskera | Castellano | Tipo | Nota |
|---|---|---|---|
| kaixo | hola | interj. |  |
| egun on | buenos días | expr. |  |
| arratsalde on | buenas tardes | expr. |  |
| gabon | buenas noches | expr. |  |
| agur | adiós | interj. |  |
| gero arte | hasta luego | expr. |  |
| bihar arte | hasta mañana | expr. |  |
| eskerrik asko | muchas gracias | expr. |  |
| mesedez | por favor | adv. |  |
| barkatu | perdón; perdona | interj. |  |
| ez horregatik | de nada | expr. |  |
| zorionak | felicidades | interj. |  |
| ondo | bien | adv. |  |
| oso | muy | adv. |  |
| bai | sí | adv. |  |
| ez | no | adv. |  |
| ni | yo | pron. |  |
| zu | tú; usted | pron. |  |
| izan | ser | verbo |  |
| izen | nombre | sust. |  |
| abizen | apellido | sust. |  |
| nor | quién | pron. |  |
| zer | qué | pron. |  |
| zein | cuál | pron. |  |
| nola | cómo | adv. |  |
<!-- vocab:1.1:end -->

#### Unidad 1.2 · Nor zara?
- **Objetivos:** presentarse y presentar a otra persona; decir de dónde es alguien y a qué se
  dedica; dar datos personales sencillos (teléfono, dirección).
- **Gramática:** pronombres personales y todo el presente de IZAN (NOR); nombres propios y
  comunes con el artículo -a/-ak; NONGO (*bilbokoa*, *nongoa zara?*); interrogativos
  NOR/ZER/ZEIN; la conjunción ALA en preguntas; demostrativos con IZENA + ERAKUSLEA
  (*neska hau*); el alfabeto y la pronunciación (tz, ts, tx, z, s, x).
- **Guía de la unidad:** cómo se busca una palabra en el diccionario (sin artículo); números del 0
  al 10 para dictar teléfonos.

<!-- vocab:1.2:start -->
**Vocabulario (22 palabras)**

| Euskera | Castellano | Tipo | Nota |
|---|---|---|---|
| hura | él; ella; aquel | pron. |  |
| gu | nosotros | pron. |  |
| zuek | vosotros | pron. |  |
| haiek | ellos; ellas | pron. |  |
| gizon | hombre | sust. |  |
| emakume | mujer | sust. |  |
| mutil | chico | sust. |  |
| neska | chica | sust. |  |
| ume | niño; niña | sust. |  |
| irakasle | profesor; profesora | sust. |  |
| ikasle | estudiante | sust. |  |
| mediku | médico; médica | sust. |  |
| erizain | enfermero; enfermera | sust. |  |
| sukaldari | cocinero; cocinera | sust. |  |
| ingeniari | ingeniero; ingeniera | sust. |  |
| dendari | dependiente; dependienta | sust. |  |
| langabetu | parado; parada | sust. |  |
| telefono | teléfono | sust. |  |
| zenbaki | número | sust. |  |
| helbide | dirección | sust. |  |
| nongo | de dónde (origen) | adv. |  |
| eta | y | conj. |  |

**Serie cerrada (no cuenta en el límite):** zero (cero), bat (uno), bi (dos), hiru (tres), lau (cuatro), bost (cinco), sei (seis), zazpi (siete), zortzi (ocho), bederatzi (nueve), hamar (diez)
<!-- vocab:1.2:end -->

### Libro 2 · Non dago? → Unidades 2.1 y 2.2

#### Unidad 2.1 · Herrian
- **Objetivos:** nombrar lugares y servicios del pueblo o la ciudad; preguntar si existe un lugar
  y dónde está.
- **Gramática:** caso NON (inesivo: *kalean*, *Bilbon*, *bankuan*); EGON en presente; los
  demostrativos *hau*, *hori*, *hura* en NON (*honetan*, *horretan*, *hartan*); BA…-(R)IK?
  (*Badago farmaziarik hemen?*); adverbios *hemen*, *hor*, *han*.

<!-- vocab:2.1:start -->
**Vocabulario (25 palabras)**

| Euskera | Castellano | Tipo | Nota |
|---|---|---|---|
| herri | pueblo | sust. |  |
| hiri | ciudad | sust. |  |
| kale | calle | sust. |  |
| plaza | plaza | sust. |  |
| parke | parque | sust. |  |
| eliza | iglesia | sust. |  |
| udaletxe | ayuntamiento | sust. |  |
| banku | banco | sust. |  |
| farmazia | farmacia | sust. |  |
| eskola | escuela | sust. |  |
| ospitale | hospital | sust. |  |
| liburutegi | biblioteca | sust. |  |
| jatetxe | restaurante | sust. |  |
| taberna | bar | sust. |  |
| hotel | hotel | sust. |  |
| geltoki | estación | sust. |  |
| hondartza | playa | sust. |  |
| mendi | monte | sust. |  |
| non | dónde | adv. |  |
| egon | estar | verbo |  |
| hau | este; esta | pron. |  |
| hori | ese; esa | pron. | homógrafo de hori (amarillo) |
| hemen | aquí | adv. |  |
| hor | ahí | adv. |  |
| han | allí | adv. |  |
<!-- vocab:2.1:end -->

#### Unidad 2.2 · Non dago?
- **Objetivos:** situar algo o a alguien en un lugar; dar y entender indicaciones sencillas para
  llegar a un sitio; describir lugares; hablar del tiempo que hace.
- **Gramática:** postposiciones de lugar con NOREN (*bankuaren ondoan*, *etxe aurrean*);
  instrucciones de dirección (*segi zuzen*, *eskuinera*); puntos cardinales; la hora (*ordu bata
  da*, *hirurak eta erdiak*); IZENA + ADJEKTIBOA (*herri polita*); expresiones del tiempo
  atmosférico (*euria ari du*, *bero egiten du*).

<!-- vocab:2.2:start -->
**Vocabulario (26 palabras)**

| Euskera | Castellano | Tipo | Nota |
|---|---|---|---|
| aurrean | delante | posp. |  |
| atzean | detrás | posp. |  |
| ondoan | al lado | posp. |  |
| gainean | encima | posp. |  |
| azpian | debajo | posp. |  |
| artean | entre | posp. |  |
| gertu | cerca | adv. |  |
| urrun | lejos | adv. |  |
| eskuin | derecha | sust. |  |
| ezker | izquierda | sust. |  |
| zuzen | recto | adv. |  |
| ipar | norte | sust. |  |
| hego | sur | sust. |  |
| ekialde | este (punto cardinal) | sust. |  |
| mendebalde | oeste | sust. |  |
| handi | grande | adj. |  |
| txiki | pequeño | adj. |  |
| polit | bonito | adj. |  |
| eguraldi | tiempo (meteorológico) | sust. |  |
| euri | lluvia | sust. |  |
| elur | nieve | sust. |  |
| eguzki | sol | sust. |  |
| haize | viento | sust. |  |
| hotz | frío | sust. |  |
| bero | calor | sust. |  |
| ordu | hora | sust. |  |
<!-- vocab:2.2:end -->

### Libro 3 · Familia eta lagunak → Unidades 3.1 y 3.2

#### Unidad 3.1 · Nire jendea
- **Objetivos:** hablar de la familia y de los amigos: quiénes son, dónde viven y con quién.
- **Gramática:** NOREN (genitivo posesivo: *nire*, *zure*, *Aneren*; con nombres comunes:
  *mutilaren*); NOREN + postposición; NOREKIN (*lagunekin*, *nirekin*); BIZI IZAN
  (*Gasteizen bizi naiz*).
- **Guía de la unidad:** *anaia/neba* y *arreba/ahizpa*: el término depende de si la persona es
  chico o chica. `TODO(revisión-nativa)`: confirmar la explicación del uso actual.

<!-- vocab:3.1:start -->
**Vocabulario (21 palabras)**

| Euskera | Castellano | Tipo | Nota |
|---|---|---|---|
| aita | padre | sust. |  |
| ama | madre | sust. |  |
| anaia | hermano (de un chico) | sust. |  |
| arreba | hermana (de un chico) | sust. |  |
| ahizpa | hermana (de una chica) | sust. |  |
| neba | hermano (de una chica) | sust. |  |
| seme | hijo | sust. |  |
| alaba | hija | sust. |  |
| aitona | abuelo | sust. |  |
| amona | abuela | sust. |  |
| senar | marido | sust. |  |
| emazte | esposa | sust. |  |
| guraso | padre o madre; padres (pl.) | sust. |  |
| osaba | tío | sust. |  |
| izeba | tía | sust. |  |
| lehengusu | primo; prima | sust. |  |
| lagun | amigo; amiga | sust. |  |
| noren | de quién | pron. |  |
| norekin | con quién | pron. |  |
| bizi izan | vivir | verbo |  |
| ukan | haber; tener (auxiliar) | verbo |  |
<!-- vocab:3.1:end -->

#### Unidad 3.2 · Nolakoa da?
- **Objetivos:** describir el aspecto físico y el carácter de una persona; pedir y dar información
  personal (edad, estado civil, si tiene hijos o hermanos).
- **Gramática:** NOLAKOA? / NOLA?; NORK (ergativo) con nombres propios y comunes; UKAN en
  presente (*dut*, *duzu*, *du*…; *daukat* como alternativa coloquial se ve en la unidad 4);
  números del 10 al 100 (*hogeita hamar*, *berrogeita bost*); ZENBAT; preguntas encadenadas de
  información personal.

<!-- vocab:3.2:start -->
**Vocabulario (17 palabras)**

| Euskera | Castellano | Tipo | Nota |
|---|---|---|---|
| altu | alto | adj. |  |
| baxu | bajo | adj. |  |
| lodi | gordo; grueso | adj. |  |
| argal | delgado | adj. |  |
| gazte | joven | adj. |  |
| zahar | viejo; mayor | adj. |  |
| ile | pelo | sust. |  |
| begi | ojo | sust. |  |
| luze | largo | adj. |  |
| motz | corto | adj. |  |
| alai | alegre | adj. |  |
| jator | majo; maja | adj. |  |
| atsegin | agradable | adj. |  |
| ezkondu | casado; casarse | verbo |  |
| ezkongabe | soltero; soltera | adj. |  |
| urte | año | sust. |  |
| zenbat | cuánto | adv. |  |

**Serie cerrada (no cuenta en el límite):** hamaika (once), hamabi (doce), hamahiru (trece), hamalau (catorce), hamabost (quince), hamasei (dieciséis), hamazazpi (diecisiete), hemezortzi (dieciocho), hemeretzi (diecinueve), hogei (veinte), berrogei (cuarenta), hirurogei (sesenta), laurogei (ochenta), ehun (cien)
<!-- vocab:3.2:end -->

---

## Sección 2 · Etxean eta kalean (En casa y en la calle)

### Libro 4 · Zure etxea → Unidades 4.1 y 4.2

#### Unidad 4.1 · Etxea
- **Objetivos:** nombrar las partes y los muebles de una casa; decir qué tiene una casa y qué hace
  falta en ella.
- **Gramática:** EDUKI en presente (*daukat*, *dauka*…); números a partir de 100 (*ehun*,
  *berrehun*, *mila*); BEHAR IZAN (*mahai bat behar dut*); el plural con *-ak* y UKAN
  (*ditut*).

<!-- vocab:4.1:start -->
**Vocabulario (21 palabras)**

| Euskera | Castellano | Tipo | Nota |
|---|---|---|---|
| etxe | casa | sust. |  |
| pisu | piso | sust. |  |
| solairu | planta (de un edificio) | sust. |  |
| logela | dormitorio | sust. |  |
| sukalde | cocina | sust. |  |
| egongela | sala de estar | sust. |  |
| bainugela | cuarto de baño | sust. |  |
| komun | váter; aseo | sust. |  |
| terraza | terraza | sust. |  |
| garaje | garaje | sust. |  |
| igogailu | ascensor | sust. |  |
| leiho | ventana | sust. |  |
| ate | puerta | sust. |  |
| eskailera | escalera | sust. |  |
| atari | portal | sust. |  |
| eduki | tener | verbo |  |
| berri | nuevo | adj. |  |
| argitsu | luminoso | adj. |  |
| ilun | oscuro | adj. |  |
| eguzkitsu | soleado | adj. |  |
| zabal | amplio; ancho | adj. |  |
<!-- vocab:4.1:end -->

#### Unidad 4.2 · Etxe bila
- **Objetivos:** pedir y dar información para alquilar, comprar o intercambiar una vivienda;
  comparar dos viviendas; hacer una lista de lo que hay que comprar para equipar una casa.
- **Gramática:** BALIO IZAN (*zenbat balio du?*); NAHI IZAN (*pisu bat alokatu nahi dut*);
  comparación -AGO (*handiagoa*, *merkeagoa*); GEHIAGO / GUTXIAGO.

<!-- vocab:4.2:start -->
**Vocabulario (22 palabras)**

| Euskera | Castellano | Tipo | Nota |
|---|---|---|---|
| alokatu | alquilar | verbo |  |
| erosi | comprar | verbo |  |
| saldu | vender | verbo |  |
| trukatu | intercambiar | verbo |  |
| merke | barato | adj. |  |
| garesti | caro | adj. |  |
| prezio | precio | sust. |  |
| euro | euro | sust. |  |
| balio izan | costar; valer | verbo |  |
| behar izan | necesitar; tener que | verbo |  |
| nahi izan | querer | verbo |  |
| gehiago | más | adv. |  |
| gutxiago | menos | adv. |  |
| ohe | cama | sust. |  |
| mahai | mesa | sust. |  |
| aulki | silla | sust. |  |
| sofa | sofá | sust. |  |
| armairu | armario | sust. |  |
| hozkailu | frigorífico | sust. |  |
| garbigailu | lavadora | sust. |  |
| labe | horno | sust. |  |
| dutxa | ducha | sust. |  |

**Serie cerrada (no cuenta en el límite):** mila (mil), berrehun (doscientos)
<!-- vocab:4.2:end -->

### Libro 5 · On egin → Unidades 5.1 y 5.2

#### Unidad 5.1 · Gustuko janaria
- **Objetivos:** hablar de la comida y la bebida; expresar gustos y preferencias.
- **Gramática:** NOR-NORI con *gustatu* (*gustatzen zait*, *gustatzen zaizkit*); caso NORI con
  nombres propios, comunes y demostrativos (*Aneri*, *umeari*, *honi*); NAHIAGO IZAN.

<!-- vocab:5.1:start -->
**Vocabulario (25 palabras)**

| Euskera | Castellano | Tipo | Nota |
|---|---|---|---|
| janari | comida | sust. |  |
| edari | bebida | sust. |  |
| ogi | pan | sust. |  |
| ur | agua | sust. |  |
| esne | leche | sust. |  |
| kafe | café | sust. |  |
| te | té | sust. |  |
| ardo | vino | sust. |  |
| garagardo | cerveza | sust. |  |
| zuku | zumo | sust. |  |
| haragi | carne | sust. |  |
| arrain | pescado | sust. |  |
| oilasko | pollo | sust. |  |
| arrautza | huevo | sust. |  |
| patata | patata | sust. |  |
| tomate | tomate | sust. |  |
| letxuga | lechuga | sust. |  |
| sagar | manzana | sust. |  |
| laranja | naranja | sust. |  |
| gazta | queso | sust. |  |
| gustatu | gustar | verbo |  |
| nahiago izan | preferir | verbo |  |
| gosari | desayuno | sust. |  |
| bazkari | comida (del mediodía) | sust. |  |
| afari | cena | sust. |  |
<!-- vocab:5.1:end -->

#### Unidad 5.2 · Sukaldean
- **Objetivos:** dar y seguir instrucciones para preparar una receta; dar consejos para comer de
  forma sana.
- **Gramática:** imperativo sin auxiliar (*moztu tomatea*); marcadores del discurso
  (*lehenengo*, *gero*, *ondoren*, *azkenik*); oraciones temporales (*… baino lehen*,
  *…-ta gero*, *… arte*); frecuencia (*egunero*, *astero*, *askotan*); BEHAR DA / BEHAR DIRA;
  nominalización -T(Z)EA (*komeni da…*, *kaltegarria da…*); causal con *eta*.

<!-- vocab:5.2:start -->
**Vocabulario (21 palabras)**

| Euskera | Castellano | Tipo | Nota |
|---|---|---|---|
| olio | aceite | sust. |  |
| gatz | sal | sust. |  |
| azukre | azúcar | sust. |  |
| moztu | cortar | verbo |  |
| egosi | cocer | verbo |  |
| frijitu | freír | verbo |  |
| nahastu | mezclar | verbo |  |
| gehitu | añadir | verbo |  |
| berotu | calentar | verbo |  |
| lapiko | olla | sust. |  |
| zartagin | sartén | sust. |  |
| plater | plato | sust. |  |
| lehenengo | primero | adv. |  |
| gero | después | adv. |  |
| ondoren | a continuación | adv. |  |
| azkenik | por último | adv. |  |
| osasuntsu | sano | adj. |  |
| kaltegarri | perjudicial | adj. |  |
| egunero | todos los días | adv. |  |
| askotan | a menudo | adv. |  |
| komeni izan | convenir | verbo |  |
<!-- vocab:5.2:end -->

### Libro 6 · Erosketak → Unidades 6.1 y 6.2

#### Unidad 6.1 · Dendan
- **Objetivos:** hacer compras cotidianas en tiendas y centros comerciales: pedir un producto,
  preguntar el precio y pagar.
- **Gramática:** verbo + NAHI IZAN (*ogia erosi nahi dut*); nombres de tiendas con *-tegi*/*-degi*;
  ZENBAT DA?

<!-- vocab:6.1:start -->
**Vocabulario (14 palabras)**

| Euskera | Castellano | Tipo | Nota |
|---|---|---|---|
| denda | tienda | sust. |  |
| okindegi | panadería | sust. |  |
| harategi | carnicería | sust. |  |
| arrandegi | pescadería | sust. |  |
| frutadenda | frutería | sust. | TODO(revisión-nativa): frutadenda vs fruta-denda |
| supermerkatu | supermercado | sust. |  |
| liburu-denda | librería | sust. |  |
| merkataritza-gune | centro comercial | sust. |  |
| saltzaile | vendedor; vendedora | sust. |  |
| bezero | cliente | sust. |  |
| ordaindu | pagar | verbo |  |
| txartel | tarjeta | sust. |  |
| eskudiru | dinero en efectivo | sust. |  |
| diru | dinero | sust. |  |
<!-- vocab:6.1:end -->

#### Unidad 6.2 · Opari bat
- **Objetivos:** hablar de ropa: describirla, dar opiniones y decir lo que se necesita; elegir y
  comprar un regalo según los gustos de otra persona.
- **Gramática:** -EGI(A) (*handiegia*, *garestiegia*); NOLAKOA(K) DA (DIRA)?; NOLA DAUKAZU?;
  NORENTZAT (*amarentzat*, *zuretzat*); nominalización -T(Z)EA con *gustatu* (*irakurtzea
  gustatzen zait*); causales -ELAKO (*merkea delako*); ZERGATIK?

<!-- vocab:6.2:start -->
**Vocabulario (22 palabras)**

| Euskera | Castellano | Tipo | Nota |
|---|---|---|---|
| arropa | ropa | sust. |  |
| alkandora | camisa | sust. |  |
| galtza | pantalón | sust. |  |
| gona | falda | sust. |  |
| soineko | vestido | sust. |  |
| jertse | jersey | sust. |  |
| beroki | abrigo | sust. |  |
| kamiseta | camiseta | sust. |  |
| zapata | zapato | sust. |  |
| neurri | talla; medida | sust. |  |
| gorri | rojo | adj. |  |
| zuri | blanco | adj. |  |
| beltz | negro | adj. |  |
| urdin | azul | adj. |  |
| berde | verde | adj. |  |
| hori (kolorea) | amarillo | adj. | homógrafo de hori (ese) |
| gris | gris | adj. |  |
| marroi | marrón | adj. |  |
| opari | regalo | sust. |  |
| urtebetetze | cumpleaños | sust. |  |
| zergatik | por qué | adv. |  |
| probatu | probar(se) | verbo |  |
<!-- vocab:6.2:end -->

---

## Sección 3 · Egunez egun (Día a día)

### Libro 7 · Zer berri? → Unidades 7.1 y 7.2

#### Unidad 7.1 · Zer egin duzu?
- **Objetivos:** contar lo que se ha hecho hace poco y preguntar por ello.
- **Gramática:** aspecto perfectivo con auxiliar NOR (*joan naiz*) y NOR-NORK (*ikusi dut*): verbos
  transitivos e intransitivos; marcadores temporales (*gaur*, *atzo*, *duela bi egun*, *orain dela
  gutxi*); NORA / NONDIK con nombres propios y comunes; nominalización -T(Z)ERA (*zertara?*);
  días de la semana.

<!-- vocab:7.1:start -->
**Vocabulario (24 palabras)**

| Euskera | Castellano | Tipo | Nota |
|---|---|---|---|
| atzo | ayer | adv. |  |
| herenegun | anteayer | adv. |  |
| gaur | hoy | adv. |  |
| goiz | mañana (parte del día) | sust. |  |
| arratsalde | tarde | sust. |  |
| gau | noche | sust. |  |
| aste | semana | sust. |  |
| hilabete | mes | sust. |  |
| duela | hace (tiempo) | adv. |  |
| joan den | pasado (la semana pasada) | adj. |  |
| iaz | el año pasado | adv. |  |
| jaio | nacer | verbo |  |
| bidaiatu | viajar | verbo |  |
| ikasi | aprender; estudiar | verbo |  |
| lan egin | trabajar | verbo |  |
| ikusi | ver | verbo |  |
| irakurri | leer | verbo |  |
| idatzi | escribir | verbo |  |
| deitu | llamar | verbo |  |
| hartu | coger; tomar | verbo |  |
| bazkaldu | comer (al mediodía) | verbo |  |
| afaldu | cenar | verbo |  |
| entzun | escuchar | verbo |  |
| galdu | perder | verbo |  |

**Serie cerrada (no cuenta en el límite):** astelehen (lunes), astearte (martes), asteazken (miércoles), ostegun (jueves), ostiral (viernes), larunbat (sábado), igande (domingo)
<!-- vocab:7.1:end -->

#### Unidad 7.2 · Zer egingo duzu?
- **Objetivos:** contar lo que se va a hacer y preguntar por ello; decir lo que alguien está haciendo
  en este momento.
- **Gramática:** aspecto futuro -KO/-GO (*joango naiz*, *egingo dut*); marcadores (*bihar*, *etzi*,
  *datorren astean*); -T(Z)EN ARI IZAN (*irakurtzen ari naiz*); verbos sintéticos en presente:
  EGON, JOAN, ETORRI, IBILI, EDUKI (*noa*, *nator*, *nabil*); orden de los verbos compuestos en
  preguntas y respuestas; meses del año.

<!-- vocab:7.2:start -->
**Vocabulario (21 palabras)**

| Euskera | Castellano | Tipo | Nota |
|---|---|---|---|
| bihar | mañana (día) | adv. |  |
| etzi | pasado mañana | adv. |  |
| datorren | próximo; que viene | adj. |  |
| aurten | este año | adv. |  |
| orain | ahora | adv. |  |
| joan | ir | verbo |  |
| etorri | venir | verbo |  |
| ibili | andar | verbo |  |
| egin | hacer | verbo |  |
| jan | comer | verbo |  |
| edan | beber | verbo |  |
| lo egin | dormir | verbo |  |
| eraman | llevar | verbo |  |
| sartu | entrar; meter | verbo |  |
| bukatu | terminar | verbo |  |
| prestatu | preparar | verbo |  |
| itzuli | volver; devolver | verbo |  |
| geratu | quedar(se) | verbo |  |
| berandu | tarde (con retraso) | adv. |  |
| bidaia-agentzia | agencia de viajes | sust. |  |
| apaindegi | peluquería | sust. |  |

**Serie cerrada (no cuenta en el límite):** urtarril (enero), otsail (febrero), martxo (marzo), apiril (abril), maiatz (mayo), ekain (junio), uztail (julio), abuztu (agosto), irail (septiembre), urri (octubre), azaro (noviembre), abendu (diciembre)
<!-- vocab:7.2:end -->

### Libro 8 · Hau buruko mina! → Unidades 8.1 y 8.2

#### Unidad 8.1 · Buruko mina
- **Objetivos:** decir qué duele y describir síntomas; pedir hora en el centro de salud.
- **Gramática:** NONGO MINA (*buruko mina daukat*); NONGO con nombres comunes y propios;
  ZER ORDUTAN? (*hiruretan*).

<!-- vocab:8.1:start -->
**Vocabulario (20 palabras)**

| Euskera | Castellano | Tipo | Nota |
|---|---|---|---|
| buru | cabeza | sust. |  |
| belarri | oreja; oído | sust. |  |
| sudur | nariz | sust. |  |
| aho | boca | sust. |  |
| hortz | diente | sust. |  |
| lepo | cuello | sust. |  |
| beso | brazo | sust. |  |
| esku | mano | sust. |  |
| hatz | dedo | sust. |  |
| sabel | vientre; tripa | sust. |  |
| bizkar | espalda | sust. |  |
| hanka | pierna | sust. |  |
| oin | pie | sust. |  |
| belaun | rodilla | sust. |  |
| min | dolor | sust. |  |
| sukar | fiebre | sust. |  |
| eztul | tos | sust. |  |
| hotzeria | resfriado | sust. |  |
| gaixo | enfermo | adj. |  |
| nekatuta | cansado | adj. |  |
<!-- vocab:8.1:end -->

#### Unidad 8.2 · Medikuarengana
- **Objetivos:** entender y dar recomendaciones para problemas de salud comunes; describir hábitos
  saludables de la vida diaria.
- **Gramática:** NORENGANA (*medikuarengana*, *zuregana*); NOLA? con -TA/-DA (*hautsi du* →
  *hautsita dauka*); condicionales con BA- (*sukarra baduzu…*); aspecto habitual (*egunero
  jaikitzen naiz*); frecuencia (*astean behin*, *noizean behin*, *inoiz ez*).

<!-- vocab:8.2:start -->
**Vocabulario (19 palabras)**

| Euskera | Castellano | Tipo | Nota |
|---|---|---|---|
| osasun-etxe | centro de salud | sust. |  |
| botika | medicina; medicamento | sust. |  |
| pilula | pastilla | sust. |  |
| hitzordu | cita | sust. |  |
| errezeta | receta (médica) | sust. |  |
| hautsi | romper | verbo |  |
| jaiki | levantarse | verbo |  |
| gosaldu | desayunar | verbo |  |
| dutxatu | ducharse | verbo |  |
| oheratu | acostarse | verbo |  |
| inoiz | alguna vez; nunca (con ez) | adv. |  |
| noizean behin | de vez en cuando | adv. |  |
| gutxitan | pocas veces | adv. |  |
| asko | mucho | adv. |  |
| bakarrik | solo; solamente | adv. |  |
| eseri | sentarse | verbo |  |
| eskatu | pedir | verbo |  |
| igeri egin | nadar | verbo |  |
| sendabelar | planta medicinal | sust. |  |
<!-- vocab:8.2:end -->

### Libro 9 · Zaindu ingurumena → Unidades 9.1 y 9.2

#### Unidad 9.1 · Birziklatu
- **Objetivos:** dar consejos para cuidar el medio ambiente: dónde tirar cada residuo y de qué
  material está hecho cada objeto.
- **Gramática:** ZEREZKOA? (*plastikozkoa*, *beirazkoa*); DAITEKE / EZIN DA; imperativo de
  3.ª persona con NOR y NOR-NORK (*bota ezazu*); imperativo indirecto (*esan dit… -tzeko*).

<!-- vocab:9.1:start -->
**Vocabulario (23 palabras)**

| Euskera | Castellano | Tipo | Nota |
|---|---|---|---|
| ingurumen | medio ambiente | sust. |  |
| natura | naturaleza | sust. |  |
| zuhaitz | árbol | sust. |  |
| baso | bosque | sust. |  |
| itsaso | mar | sust. |  |
| ibai | río | sust. |  |
| kutsadura | contaminación | sust. |  |
| energia | energía | sust. |  |
| argi | luz | sust. |  |
| birziklatu | reciclar | verbo |  |
| edukiontzi | contenedor | sust. |  |
| zakarrontzi | papelera; cubo de basura | sust. |  |
| plastiko | plástico | sust. |  |
| beira | vidrio | sust. |  |
| paper | papel | sust. |  |
| kartoi | cartón | sust. |  |
| botila | botella | sust. |  |
| poltsa | bolsa | sust. |  |
| hondakin | residuo | sust. |  |
| egur | madera | sust. |  |
| burdina | hierro | sust. |  |
| gela | habitación; aula | sust. |  |
| leku | lugar | sust. |  |
<!-- vocab:9.1:end -->

#### Unidad 9.2 · Ingurumena zaindu
- **Objetivos:** conocer medidas para proteger el medio ambiente y opinar sobre ellas; contar qué
  hacemos para cuidarlo.
- **Gramática:** nominalización -T(Z)EA (*garrantzitsua da…*); EZIN DA (impersonal, NOR y
  NOR-NORK); DEBEKATUTA DAGO; dar opinión (*ados nago*, *ez nago ados*, *nire ustez*);
  oraciones temporales con -(E)NEAN (*etxera iristen naizenean*).

<!-- vocab:9.2:start -->
**Vocabulario (11 palabras)**

| Euskera | Castellano | Tipo | Nota |
|---|---|---|---|
| zaindu | cuidar | verbo |  |
| aurreztu | ahorrar | verbo |  |
| itzali | apagar | verbo |  |
| piztu | encender | verbo |  |
| erabili | usar | verbo |  |
| bota | tirar | verbo |  |
| debekatuta | prohibido | adj. |  |
| ados | de acuerdo | adv. |  |
| uste izan | creer; opinar | verbo |  |
| iritzi | opinión | sust. |  |
| ezin | no poder | adv. |  |
<!-- vocab:9.2:end -->

---

## Sección 4 · Mundura begira (Mirando al mundo)

### Libro 10 · Hiria ala herria? → Unidades 10.1 y 10.2

#### Unidad 10.1 · Hiria ala herria?
- **Objetivos:** hablar de lo bueno y lo malo de vivir en una ciudad o en un pueblo.
- **Gramática:** comparativas A + B *baino* …-AGO(A)(K); *gehiago / gutxiago*; formas irregulares
  (*hobe*, *gehiago*); HAIN / HAINBESTE; superlativo …-(R)IK / …-EN(A)(K) (*herririk
  politena*).

<!-- vocab:10.1:start -->
**Vocabulario (24 palabras)**

| Euskera | Castellano | Tipo | Nota |
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
| zerbitzu | servicio | sust. |  |
| garraio | transporte | sust. |  |
| autobus | autobús | sust. |  |
| metro | metro | sust. |  |
| tren | tren | sust. |  |
| kiroldegi | polideportivo | sust. |  |
| zinema | cine | sust. |  |
| antzoki | teatro (edificio) | sust. |  |
| museo | museo | sust. |  |
| igerileku | piscina | sust. |  |
| aparkaleku | aparcamiento | sust. |  |
<!-- vocab:10.1:end -->

#### Unidad 10.2 · Bizitzeko lekua
- **Objetivos:** opinar sobre la calidad de vida de un lugar; contar dónde nos gusta pasar las
  vacaciones.
- **Gramática:** completivas -ELA (*uste dut…*, *iruditzen zait…*, *esan du…*); comparativas
  negativas (*ez da … bezain*, A + *ez* + B *baino* …-ago); -T(Z)EA con *gustatu* en pasado.
  `TODO(revisión-nativa)`: concretar la forma de pasado de *gustatu* que se enseña en A1.

<!-- vocab:10.2:start -->
**Vocabulario (18 palabras)**

| Euskera | Castellano | Tipo | Nota |
|---|---|---|---|
| trafiko | tráfico | sust. |  |
| zarata | ruido | sust. |  |
| bizi-kalitate | calidad de vida | sust. |  |
| opor | vacaciones (oporrak) | sust. |  |
| bidaia | viaje | sust. |  |
| kanpin | camping | sust. |  |
| atzerri | extranjero | sust. |  |
| jende | gente | sust. |  |
| auzo | barrio | sust. |  |
| on | bueno | adj. |  |
| txar | malo | adj. |  |
| hobe | mejor | adj. |  |
| iruditu | parecer | verbo |  |
| egoki | adecuado | adj. |  |
| turista | turista | sust. |  |
| sarbide | acceso | sust. |  |
| pasatu | pasar | verbo |  |
| primeran | estupendamente | adv. |  |
<!-- vocab:10.2:end -->

### Libro 11 · Lan eta lan → Unidades 11.1 y 11.2

#### Unidad 11.1 · Lanbideak
- **Objetivos:** pedir y dar información sobre un trabajo (horario, sueldo, contrato) y sobre lo que
  sabe hacer una persona.
- **Gramática:** JAKIN en presente (*dakit*, *dakizu*); -T(Z)EN con *jakin* (*badakizu
  gidatzen?*); NORAKO; NOR-NORI-NORK en presente, todas las personas (*diot*, *didazu*…).

<!-- vocab:11.1:start -->
**Vocabulario (15 palabras)**

| Euskera | Castellano | Tipo | Nota |
|---|---|---|---|
| lan | trabajo | sust. |  |
| lanbide | profesión | sust. |  |
| enpresa | empresa | sust. |  |
| bulego | oficina | sust. |  |
| lantegi | fábrica; taller | sust. |  |
| nagusi | jefe; jefa | sust. |  |
| lankide | compañero de trabajo | sust. |  |
| langile | trabajador; trabajadora | sust. |  |
| soldata | sueldo | sust. |  |
| ordutegi | horario | sust. |  |
| kontratu | contrato | sust. |  |
| lanaldi | jornada | sust. |  |
| iragarki | anuncio | sust. |  |
| gidari | conductor; conductora | sust. |  |
| idazkari | secretario; secretaria | sust. |  |
<!-- vocab:11.1:end -->

#### Unidad 11.2 · Lan bila
- **Objetivos:** valorar si una persona encaja en un puesto (formación, idiomas, experiencia);
  hablar de experiencias laborales anteriores.
- **Gramática:** pregunta indirecta (EA) …-(E)N (*galdetu dit ea ingelesa dakidan*); NORENGANDIK;
  -T(Z)EN con *ikasi* e *irakatsi* (*gidatzen ikasi du*).

<!-- vocab:11.2:start -->
**Vocabulario (15 palabras)**

| Euskera | Castellano | Tipo | Nota |
|---|---|---|---|
| jakin | saber | verbo |  |
| eman | dar | verbo |  |
| esan | decir | verbo |  |
| ekarri | traer | verbo |  |
| erakutsi | enseñar (mostrar) | verbo |  |
| bidali | enviar | verbo |  |
| galdetu | preguntar | verbo |  |
| erantzun | responder | verbo |  |
| irakatsi | enseñar (instruir) | verbo |  |
| esperientzia | experiencia | sust. |  |
| hizkuntza | idioma | sust. |  |
| ingeles | inglés | sust. |  |
| gidabaimen | carné de conducir | sust. | TODO(revisión-nativa): gidabaimen vs gidatzeko baimen |
| ordenagailu | ordenador | sust. |  |
| libre | libre | adj. |  |
<!-- vocab:11.2:end -->

### Libro 12 · Aisialdia → Unidades 12.1 y 12.2

#### Unidad 12.1 · Denbora librea
- **Objetivos:** pedir y dar información sobre actividades y cursos de tiempo libre: cuándo, dónde
  y a qué hora son.
- **Gramática:** NOIZ = NON (*astelehenean*, *urrian*); ZER ORDUTATIK ZER ORDUTARA; NOLA con el
  instrumental -Z (*autoz*, *trenez*); el indeterminado (*mugagabea*); NORAINO.

<!-- vocab:12.1:start -->
**Vocabulario (22 palabras)**

| Euskera | Castellano | Tipo | Nota |
|---|---|---|---|
| aisialdi | tiempo libre | sust. |  |
| kirol | deporte | sust. |  |
| futbol | fútbol | sust. |  |
| saskibaloi | baloncesto | sust. |  |
| igeriketa | natación | sust. |  |
| txirrindularitza | ciclismo | sust. |  |
| dantza | danza; baile | sust. |  |
| musika | música | sust. |  |
| kontzertu | concierto | sust. |  |
| antzerki | teatro (obra) | sust. |  |
| film | película | sust. |  |
| liburu | libro | sust. |  |
| marraztu | dibujar | verbo |  |
| abestu | cantar | verbo |  |
| jolastu | jugar | verbo |  |
| noiz | cuándo | adv. |  |
| asteburu | fin de semana | sust. |  |
| jai | fiesta; día festivo | sust. |  |
| ikastaro | curso | sust. |  |
| argazki | foto | sust. |  |
| sukaldaritza | cocina (actividad) | sust. |  |
| izena eman | apuntarse; inscribirse | verbo |  |
<!-- vocab:12.1:end -->

#### Unidad 12.2 · Asmoak
- **Objetivos:** proponer planes de ocio; expresar ganas, intenciones y condiciones; contar
  costumbres del tiempo libre en el pasado.
- **Gramática:** -T(Z)EKO GOGOA EDUKI/UKAN; BA… -T(Z)EKO GOGORIK?; -T(Z)EKO ASMOA
  EDUKI/UKAN; porcentajes; condición real -(T)ZEN BA…; IZAN y UKAN en pasado (*nintzen*,
  *nuen*); aspecto habitual en pasado (*joaten nintzen*).

<!-- vocab:12.2:start -->
**Vocabulario (10 palabras)**

| Euskera | Castellano | Tipo | Nota |
|---|---|---|---|
| txango | excursión | sust. |  |
| auto | coche | sust. |  |
| bizikleta | bicicleta | sust. |  |
| hegazkin | avión | sust. |  |
| oinez | a pie | adv. |  |
| noraino | hasta dónde | adv. |  |
| gogo | ganas | sust. |  |
| asmo | intención; plan | sust. |  |
| ehuneko | porcentaje; por ciento | sust. |  |
| gustuko izan | gustar (tener como preferido) | verbo |  |
<!-- vocab:12.2:end -->

---

## 4. Pendiente de revisión

- Las palabras de `vocabulary.csv` con nota `TODO(revisión-nativa)`.
- Algunas palabras (p. ej. *frutadenda*, *gidabaimen*, *hotzeria*) no aparecen en el texto del
  cuaderno y conviene confirmar la forma preferida en batua.
- En M7, cada lección se redacta de nuevo partiendo de estos objetivos; el validador rechazará
  palabras que no estén en este temario ni en unidades anteriores.
