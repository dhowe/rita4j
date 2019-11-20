package rita;


public class Stemmer
{

	/* Words that are both singular and plural */
	private static final String[] categorySP = {"acoustics", "aesthetics", "aquatics", "basics", "ceramics", "classics", "cosmetics", "dialectics", "deer", "dynamics", "ethics", "harmonics", "heroics", "mechanics", "metrics", "optics", "people", "physics", "polemics", "pyrotechnics", "quadratics", "quarters", "statistics", "tactics", "tropics"};

	/* Words that end in "-se" in their plural forms (like "nurse" etc.) */
	private static final String[] categorySE_SES = {"abuses", "apocalypses", "blouses", "bruises", "chaises", "cheeses", "chemises", "clauses", "corpses", "courses", "crazes", "creases", "cruises", "curses", "databases", "dazes", "defenses", "demises", "discourses", "diseases", "doses", "eclipses", "enterprises", "expenses", "friezes", "fuses", "glimpses", "guises", "hearses", "horses", "houses", "impasses", "impulses", "kamikazes", "mazes", "mousses", "noises", "nooses", "noses", "nurses", "obverses", "offenses", "oozes", "overdoses", "phrases", "posses", "premises", "pretenses", "proteases", "pulses", "purposes", "purses", "racehorses", "recluses", "recourses", "relapses", "responses", "roses", "ruses", "spouses", "stripteases", "subleases", "sunrises", "tortoises", "trapezes", "treatises", "toes", "universes", "uses", "vases", "verses", "vises", "wheelbases", "wheezes"};

	/* Words that do not have a distinct plural form (like "atlas" etc.) */
	private static final String[] category00 = {"alias", "asbestos", "atlas", "barracks", "bathos", "bias", "breeches", "britches", "canvas", "chaos", "clippers", "contretemps", "corps", "cosmos", "crossroads", "diabetes", "ethos", "gallows", "gas", "graffiti", "headquarters", "herpes", "high-jinks", "innings", "jackanapes", "lens", "means", "measles", "mews", "mumps", "news", "pathos", "pincers", "pliers", "proceedings", "rabies", "rhinoceros", "sassafras", "scissors", "series", "shears", "species", "tuna"};

	/* Words that change from "-um" to "-a" (like "curriculum" etc.), listed in their plural forms */
	private static final String[] categoryUM_A = {"addenda", "agenda", "aquaria", "bacteria", "candelabra", "compendia", "consortia", "crania", "curricula", "data", "desiderata", "dicta", "emporia", "enconia", "errata", "extrema", "gymnasia", "honoraria", "interregna", "lustra", "maxima", "media", "memoranda", "millenia", "minima", "momenta", "memorabilia", "millennia", "optima", "ova", "phyla", "quanta", "rostra", "spectra", "specula", "septa", "stadia", "strata", "symposia", "trapezia", "ultimata", "vacua", "vela"};

	/* Words that change from "-on" to "-a" (like "phenomenon" etc.), listed in their plural forms */
	private static final String[] categoryON_A = {"aphelia", "asyndeta", "automata", "criteria", "hyperbata", "noumena", "organa", "perihelia", "phenomena", "prolegomena", "referenda"};

	/* Words that change from "-o" to "-i" (like "libretto" etc.), listed in their plural forms */
	private static final String[] categoryO_I = {"alti", "bassi", "canti", "concerti", "contralti", "crescendi", "libretti", "soli", "soprani", "tempi", "virtuosi"};

	/*  Words that change from "-us" to "-i" (like "fungus" etc.), listed in their plural forms		 */
	private static final String[] categoryUS_I = {"alumni", "bacilli", "cacti", "foci", "fungi", "genii", "hippopotami", "incubi", "nimbi", "nuclei", "nucleoli", "octopi", "radii", "stimuli", "styli", "succubi", "syllabi", "termini", "tori", "umbilici", "uteri"};

	/* Words that change from "-ix" to "-ices" (like "appendix" etc.), listed in their plural forms */
	private static final String[] categoryIX_ICES = {"appendices", "cervices", "indices", "matrices"};

	/* Words that change from "-is" to "-es" (like "axis" etc.), listed in their plural forms, plus everybody ending in theses */
	private static final String[] categoryIS_ES = {"analyses", "axes", "bases", "catharses", "crises", "diagnoses", "ellipses", "emphases", "neuroses", "oases", "paralyses", "prognoses", "synopses"};

	/* Words that change from "-oe" to "-oes" (like "toe" etc.), listed in their plural forms*/
	private static final String[] categoryOE_OES = {"aloes", "backhoes", "beroes", "canoes", "chigoes", "cohoes", "does", "felloes", "floes", "foes", "gumshoes", "hammertoes", "hoes", "hoopoes", "horseshoes", "leucothoes", "mahoes", "mistletoes", "oboes", "overshoes", "pahoehoes", "pekoes", "roes", "shoes", "sloes", "snowshoes", "throes", "tic-tac-toes", "tick-tack-toes", "ticktacktoes", "tiptoes", "tit-tat-toes", "toes", "toetoes", "tuckahoes", "woes"};

	/* Words that change from "-ex" to "-ices" (like "index" etc.), listed in their plural forms*/
	private static final String[] categoryEX_ICES = {"apices", "codices", "cortices", "indices", "latices", "murices", "pontifices", "silices", "simplices", "vertices", "vortices"};

	/* Words that change from "-u" to "-us" (like "emu" etc.), listed in their plural forms*/
	private static final String[] categoryU_US = {"menus", "gurus", "apercus", "barbus", "cornus", "ecrus", "emus", "fondus", "gnus", "iglus", "mus", "nandus", "napus", "poilus", "quipus", "snafus", "tabus", "tamandus", "tatus", "timucus", "tiramisus", "tofus", "tutus"};

	/* Words that change from "-sse" to "-sses" (like "finesse" etc.), listed in their plural forms,plus those ending in mousse*/
	private static final String[] categorySSE_SSES = {"bouillabaisses", "coulisses", "crevasses", "crosses", "cuisses", "demitasses", "ecrevisses", "fesses", "finesses", "fosses", "impasses", "lacrosses", "largesses", "masses", "noblesses", "palliasses", "pelisses", "politesses", "posses", "tasses", "wrasses"};

	/* Words that change from "-che" to "-ches" (like "brioche" etc.), listed in their plural forms*/
	private static final String[] categoryCHE_CHES = {"adrenarches", "attaches", "avalanches", "barouches", "brioches", "caches", "caleches", "caroches", "cartouches", "cliches", "cloches", "creches", "demarches", "douches", "gouaches", "guilloches", "headaches", "heartaches", "huaraches", "menarches", "microfiches", "moustaches", "mustaches", "niches", "panaches", "panoches", "pastiches", "penuches", "pinches", "postiches", "psyches", "quiches", "schottisches", "seiches", "soutaches", "synecdoches", "thelarches", "troches"};

	/* Words that end with "-ics" and do not exist as nouns without the "s" (like "aerobics" etc.)*/
	private static final String[] categoryICS = {"aerobatics", "aerobics", "aerodynamics", "aeromechanics", "aeronautics", "alphanumerics", "animatronics", "apologetics", "architectonics", "astrodynamics", "astronautics", "astrophysics", "athletics", "atmospherics", "autogenics", "avionics", "ballistics", "bibliotics", "bioethics", "biometrics", "bionics", "bionomics", "biophysics", "biosystematics", "cacogenics", "calisthenics", "callisthenics", "catoptrics", "civics", "cladistics", "cryogenics", "cryonics", "cryptanalytics", "cybernetics", "cytoarchitectonics", "cytogenetics", "diagnostics", "dietetics", "dramatics", "dysgenics", "econometrics", "economics", "electromagnetics", "electronics", "electrostatics", "endodontics", "enterics", "ergonomics", "eugenics", "eurhythmics", "eurythmics", "exodontics", "fibreoptics", "futuristics", "genetics", "genomics", "geographics", "geophysics", "geopolitics", "geriatrics", "glyptics", "graphics", "gymnastics", "hermeneutics", "histrionics", "homiletics", "hydraulics", "hydrodynamics", "hydrokinetics", "hydroponics", "hydrostatics", "hygienics", "informatics", "kinematics", "kinesthetics", "kinetics", "lexicostatistics", "linguistics", "lithoglyptics", "liturgics", "logistics", "macrobiotics", "macroeconomics", "magnetics", "magnetohydrodynamics", "mathematics", "metamathematics", "metaphysics", "microeconomics", "microelectronics", "mnemonics", "morphophonemics", "neuroethics", "neurolinguistics", "nucleonics", "numismatics", "obstetrics", "onomastics", "orthodontics", "orthopaedics", "orthopedics", "orthoptics", "paediatrics", "patristics", "patristics", "pedagogics", "pediatrics", "periodontics", "pharmaceutics", "pharmacogenetics", "pharmacokinetics", "phonemics", "phonetics", "phonics", "photomechanics", "physiatrics", "pneumatics", "poetics", "politics", "pragmatics", "prosthetics", "prosthodontics", "proteomics", "proxemics", "psycholinguistics", "psychometrics", "psychonomics", "psychophysics", "psychotherapeutics", "robotics", "semantics", "semiotics", "semitropics", "sociolinguistics", "stemmatics", "strategics", "subtropics", "systematics", "tectonics", "telerobotics", "therapeutics", "thermionics", "thermodynamics", "thermostatics"};

	/* Words that change from "-ie" to "-ies" (like "auntie" etc.), listed in their plural forms*/
	private static final String[] categoryIE_IES = {"aeries", "anomies", "aunties", "baddies", "beanies", "birdies", "bogies", "bonhomies", "boogies", "bookies", "booties", "bourgeoisies", "brasseries", "brassies", "brownies", "caddies", "calories", "camaraderies", "charcuteries", "collies", "commies", "cookies", "coolies", "coonties", "cooties", "coteries", "cowpies", "cowries", "cozies", "crappies", "crossties", "curies", "darkies", "dearies", "dickies", "dies", "dixies", "doggies", "dogies", "eyries", "faeries", "falsies", "floozies", "folies", "foodies", "freebies", "gendarmeries", "genies", "gillies", "goalies", "goonies", "grannies", "groupies", "hippies", "hoagies", "honkies", "indies", "junkies", "kelpies", "kilocalories", "laddies", "lassies", "lies", "lingeries", "magpies", "magpies", "mashies", "mealies", "meanies", "menageries", "mollies", "moxies", "neckties", "newbies", "nighties", "nookies", "oldies", "panties", "patisseries", "pies", "pinkies", "pixies", "porkpies", "potpies", "prairies", "preemies", "pyxies", "quickies", "reveries", "rookies", "rotisseries", "scrapies", "sharpies", "smoothies", "softies", "stoolies", "stymies", "swaggies", "sweeties", "talkies", "techies", "ties", "tooshies", "toughies", "townies", "veggies", "walkie-talkies", "wedgies", "weenies", "yuppies", "zombies"};

	/* Maps irregular Germanic English plural nouns to their singular form */
	private static final String[] categoryIRR = {"blondes", "blonde", "teeth", "tooth", "beefs", "beef", "brethren", "brother", "busses", "bus", "cattle", "cow", "children", "child", "corpora", "corpus", "femora", "femur", "genera", "genus", "genies", "genie", "genii", "genie", "lice", "louse", "mice", "mouse", "mongooses", "mongoose", "monies", "money", "octopodes", "octopus", "oxen", "ox", "people", "person", "schemata", "schema", "soliloquies", "soliloquy", "taxis", "taxi", "throes", "throes", "trilbys", "trilby", "innings", "inning", "alibis", "alibi", "skis", "ski", "safaris", "safari", "rabbis", "rabbi"};


	
  public String stem(String word)
  {
    // TODO Auto-generated method stub
    return null;
  }

}
