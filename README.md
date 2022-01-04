# pgr203eksamen
![Java CI with Maven](https://github.com/kristiania/pgr203eksamen-GeorgTollefsen/workflows/Java%20CI%20with%20Maven/badge.svg)
<h2>HttpServer</h2>
Eksamensbesvarelse høsten 2020.
<h3>Beskrivelse</h3>
HttpServer i java, som gjennom sockets, serverer htmlkode til web-browseren.
Applikasjonen er et prosjekthåndteringssytem der man kan opprette/endre/slette medlemmer, prosjekter og oppgaver.
Medlemmer kan kobles til både prosjekter og oppgaver.
Datan lagres i en postgreSQL-database og akseseres fra serveren gjennom controllere og data access objecter.
<br>
<h3>Hvordan kjøre programmet</h3>
- mvn package: Dette lager en .jar fil.<br>
- Opprett en fil med navn pgr203.properties med dataSource.url/-username/-password lik din lokale postgreSQL database<br>
- Kjør jar fila fra samme mappe som du har pgr203.properties<br>
- eks: $ java -Dfile.encoding=utf-8 -jar EksamenPGR203-1.0-SNAPSHOT.jar<br>
 ("-Dfile.encoding=utf-8": For å lese UTF-8 tegn som "æøå")<br>
<br>
<h3>Fuksjonalitet</h3>
Serveren kjører på localhost:8080 for å komme til hovedsiden fungerer URL:<br>
-localhost:8080<br>
-localhost:8080/<br>
-localhost:8080/index.html<br>
Hvis databasen er tom kan man kun velge å opprette et prosjektmedlem.
Deretter kan man opprette/slette/vise medlemmer eller opprette prosjekter.
Videre kan man redigere/vise prosjekter eller opprette oppgaver til et prosjekt.
Man kan logge inn som en bruker med email og passord for å se sine tasks.
Etter man har logget inn en gang vil det bli satt en cookie som gjør at man ikke trenger skrive inn passord igjen, med mindre man logger ut.
<br>
<h3>Design</h3>
Bruker klikker i browser, request sendes til server som svarer med html side.
Med javascript, fetcher siden request til server for å få tak i data å vise.
Requesten blir sendt til den rette controlleren, avhengig av requesten, 
som bruker en DAO for å sende en SQL spørring til databasen.
Deretter blir datan sendt tilbake til DAOen, som sender det som et object til kontrolleren,
som sender objectinformasjonene i en httpRespons til serveren som sender htmlkode til browseren,
som viser bruker det den ville se.<br>
(Dette var et skriftlig forsøk på et sekvensdiagram)
<br>
<img src="/dbDiagram.png">
Bilde: Database ER diagram<br>
<br>
<h3>Ekstra funksjonalitet:</h3>
-Håndtering av request target "/"<br>
-5 tabeller i databasen<br>
-Mulighet til å redigere oppgaver og prosjekter<br>
-Implementasjon av cookies <br>
"Sign in": "lagrer" bruker sesjonen så man ikke trenger å skrive brukernavn og passord igjen.<br>
-Rammeverk rundt Http-håndtering<br>
  (Abstract HttpMessage, subklasser request og response)<br>
-God bruk av DAO pattern<br>
-God bruk av controller pattern<br>
-Korretk håndtering av norske tegn i HTTP <br>
-Automatisk rapportering av testdekning i Github Actions<br>
<br>
Annet:<br>
-Implementerte /echo requesthåndtering fra innleveringsoppgavene
-Skriftlig beskrivelse av arketektur<br>
-ER diagram av databasen<br>
-Man kan opprette flere prosjekter som har sine tasks og members<br>
-Bruk av Querystring klasse for å hente verdier fra request body<br>
-Passord klasse for MD5hashing av passord og cookie.<br>
-Bruk av flere http status koder: 200, 201, 204(fjernet), 302, 404<br>
-Redigering av data fra alle kolonner i projects og task tabellene.<br>
-Delete fra tabeller i databasen<br>
-Filtrering av prosjekter etter medlem<br>
-Fetch på hovedsiden sjekker databasen og gir linker deretter <br>
  Eks, finnes ingen task i databasen, finnes ikke "view task" knappen.<br>
<br>

<h3>Erfaringer med arbeidet og løsningen:</h3>

Parprogrammering i form av Test Driven Development fungerte godt til å begynne med. 
Positivt: Det var en fin måte for begge å få oversikt over koden og sammen finne ut hvordan man skulle få ting til å fungere. La opp for kontrollering av eldre kode hver gang vi implementerte noe nytt, eller gjorde endringer.
Negativt: Det krevde at vi begge kunne jobbe samtidig, noe som ikke alltid er like lett pga jobb og familie. Og når vi da jobbet, selv om 2 hoder tenker bedre enn ett, føltes det litt som at 2 gjorde jobben for én, altså at det ikke var så effektivt.
<br><br>
Vi landet på en slags hybrid, der vi først skrev masse issues og vekslet litt mellom å jobbe i hver vår branch med hvert vårt issue. For så å ha møte der vi oppdaterte hverandre med hva vi hadde gjort og hvordan koden fungerte. Eller jobbe sammen med delt skjerm, når timeplanene våre tillot det. Serlig de gangene vi sto fast eller ikke fra før hadde blitt enige om hvordan vi skulle legge opp koden.
<br><br>
Vi har begge lært veldig mye i løpet av eksamen, og erfart at refakturering underveis er viktigere enn vi først trodde. Dette kunne spart oss for mye tid. Vi tenkte at det var lurt å bare få spydd ut så mye som mulig funksjonalitet først, deretter refakturere for gjenbruk av kode. "Vi gjør det vi vet hvordan man gjør først, så finner vi ut av det vi ikke kan etterpå". Dette førte til at vi har gjort "samme jobben" flere ganger. 
<br><br>
Eks: Etter å ha laget funksjonalitet for å handle x antall requester, la vi opp for kontrollere, så implementerete vi AbstractDao, så dele vi opp HttpMessage i response og request. Dette gjorde at vi måtte skrive mye av det samme igjen og igjen, i motsetning til om vi hadde implementert bare litt funksjonalitet først, så lagt opp klassene på en fornuftig måte. Da kunne resten av funksjonaliteten bli skrevet direkte inn på den måten som den endte opp til slutt. 
<br><br>
Grunnen til at dette skjedde er litt pga den begrensede kunnskapen vi hadde om java-prosjekter før vi startet. Feks hvordan dele opp klasser og utnytte abstraktklasser. Dette er prinsipper vi har lært tidligere, men ikke kjent nytten av i praksis pga prosjektstørrelse. Nå har vi også erfart og lært at hvis man bruker litt tid på implementerer dette tidligere i prosjektet kan man spare seg for veldig mye arbeid senere. 
<br><br>
En utfordring i prosjektet har vært det at vi lager noe som vi fra før av ikke helt vet hvordan fungerer. En erfaring der er hvor viktig det er å sette seg godt inn i hvordan et system fungerer før man begynner å jobbe med det, slik at arbeidet skal gå lettere. Utfordringen er å planlegge/forstå hvor mye tid man bør bruke på hva, og hva man faktisk trenger å lære seg eller forstå. Vi er veldig FOR Johannes sin filosofi om å gjøre noe først og lære det etterpå, men i dette prosjektet bommet vi litt på balansen. Vi gjorde kanskje litt for mye funksjonalitet før vi satte oss inn i hvordan det fungerte for å kunne refakturere til en mye cleanere kode. Dette har ført til mer arbeid, men også MYE læring. Så vanskelig å si helt sikkert om det er negativt eller positivt.
<br><br>
Ca midtveis i prosjektet gikk TDD litt i glemmeboka, etter vi følte at vi hadde kontroll. Og manuel testing i en kjørende server virket enklere. Tanken var kanskje at om man tester manuelt, trenger man ikke gjøre om på tester etter refaturering og endring av kode/oppsett. Og det virker som om det går fortere å bare kjøre serveren og teste det man nettopp har skrevet. Mye av koden var også utvidelser av noe vi allerede hadde tester for og vi visste at fungerte. 
<br><br>
I ettertid forstår vi nytten av tester og at hvis vi hadde klart å jobbe som nevt tidligere med å refaturere før det ble for mye funksjonalitet, så hadde det spart oss for tid om vi skrev alle tester med en gang og rettet de underveis.
<br><br>
Det å samarbeide over nett pga Covid19 har kun vært positivt. Discord for kommuniksjon er skjermdeling fungerer på mange måter bedre enn å sitte fysisk sammen og titte på hverandres skjermer. For ikke å snakke om tiden spart i reisevei. Vi har også blitt mye stødigere på Git, og erfart fordelene med å kunne jobbe i forskjellige branch. Feks når plutselig ingenting fungerer lenger..
<br><br>
Alt i alt er den endelige erfaringen at planlegging og strukturering av tid er viktig. Jo mer man gjør av det, jo mer tid sparer man i arbeidsprossessen. Utfordringen er å vite hvor mye tid man skal bruke på det, før man ville spart tid på å bare satt i gang å jobbet i stedet.
<br>





