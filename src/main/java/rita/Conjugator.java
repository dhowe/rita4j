package rita;

import static rita.RiTa.opts;

import java.util.*;
import java.util.regex.Pattern;

public class Conjugator {
	private static String[] verbsEndingInE = new String[] { };

	private static String[] verbsEndingInDoubles = new String[] { };

	public static final String[] IRREG_VERBS_LEX = {
			"abetted", "abet", "abetting", "abet", "abhorred", "abhor", "abhorring", "abhor", "abode", "abide", "accompanied", "accompany", "acidified",
			"acidify", "acquitted", "acquit", "acquitting", "acquit", "addrest", "address", "admitted", "admit", "admitting", "admit", "allotted", "allot",
			"allotting", "allot", "am", "be", "amplified", "amplify", "annulled", "annul", "annulling", "annul", "applied", "apply", "arcked", "arc",
			"arcking", "arc", "are", "be", "arisen", "arise", "arose", "arise", "ate", "eat", "atrophied", "atrophy", "awoke", "awake", "awoken", "awake",
			"bade", "bid", "bagged", "bag", "bagging", "bag", "bandied", "bandy", "banned", "ban", "banning", "ban", "barred", "bar", "barrelled", "barrel",
			"barrelling", "barrel", "barring", "bar", "batted", "bat", "batting", "bat", "beaten", "beat", "beautified", "beautify", "became", "become",
			"bed", "bed", "bedded", "bed", "bedding", "bed", "bedevilled", "bedevil", "bedevilling", "bedevil", "been", "be", "befallen", "befall",
			"befell", "befall", "befitted", "befit", "befitting", "befit", "began", "begin", "begat", "beget", "begetting", "beget", "begged", "beg",
			"begging", "beg", "beginning", "begin", "begot", "beget", "begotten", "beget", "begun", "begin", "beheld", "behold", "beholden", "behold",
			"belying", "belie", "benefitted", "benefit", "benefitting", "benefit", "bent", "bend", "bespoke", "bespeak", "bespoken", "bespeak", "betted",
			"bet", "betting", "bet", "bevelled", "bevel", "bevelling", "bevel", "biassed", "bias", "biassing", "bias", "bidden", "bid", "bidding", "bid",
			"bit", "bite", "bitted", "bit", "bitten", "bite", "bitting", "bit", "bled", "bleed", "blest", "bless", "blew", "blow", "blipped", "blip",
			"blipping", "blip", "blotted", "blot", "blotting", "blot", "blown", "blow", "blurred", "blur", "blurring", "blur", "bore", "bear", "born",
			"bear", "borne", "bear", "bought", "buy", "bound", "bind", "bragged", "brag", "bragging", "brag", "bred", "breed", "broke", "break", "broken",
			"break", "brought", "bring", "browbeaten", "browbeat", "budded", "bud", "budding", "bud", "bugged", "bug", "bugging", "bug", "built", "build",
			"bullied", "bully", "bummed", "bum", "bumming", "bum", "buried", "bury", "burnt", "burn", "bypast", "bypass", "calcified", "calcify", "came",
			"come", "cancelled", "cancel", "cancelling", "cancel", "canned", "can", "canning", "can", "capped", "cap", "capping", "cap", "carried", "carry",
			"caught", "catch", "certified", "certify", "channelled", "channel", "channelling", "channel", "charred", "char", "charring", "char", "chatted",
			"chat", "chatting", "chat", "chid", "chide", "chidden", "chide", "chinned", "chin", "chinning", "chin", "chiselled", "chisel", "chiselling",
			"chisel", "chopped", "chop", "chopping", "chop", "chose", "choose", "chosen", "choose", "chugged", "chug", "chugging", "chug", "clad", "clothe",
			"clarified", "clarify", "classified", "classify", "clipped", "clip", "clipping", "clip", "clogged", "clog", "clogging", "clog", "clung",
			"cling", "co-ordinate", "coordinate", "co-ordinated", "coordinate", "co-ordinates", "coordinate", "co-ordinating", "coordinate", "codified",
			"codify", "combatted", "combat", "combatting", "combat", "committed", "commit", "committing", "commit", "compelled", "compel", "compelling",
			"compel", "complied", "comply", "concurred", "concur", "concurring", "concur", "conferred", "confer", "conferring", "confer", "controlled",
			"control", "controlling", "control", "copied", "copy", "corralled", "corral", "corralling", "corral", "counselled", "counsel", "counselling",
			"counsel", "crammed", "cram", "cramming", "cram", "crept", "creep", "cried", "cry", "cropped", "crop", "cropping", "crop", "crucified",
			"crucify", "cupped", "cup", "cupping", "cup", "curried", "curry", "curst", "curse", "cutting", "cut", "dallied", "dally", "dealt", "deal",
			"decried", "decry", "deferred", "defer", "deferring", "defer", "defied", "defy", "demurred", "demur", "demurring", "demur", "denied", "deny",
			"deterred", "deter", "deterring", "deter", "detoxified", "detoxify", "dialled", "dial", "dialling", "dial", "did", "do", "digging", "dig",
			"dignified", "dignify", "dimmed", "dim", "dimming", "dim", "dipped", "dip", "dipping", "dip", "dirtied", "dirty", "dispelled", "dispel",
			"dispelling", "dispel", "disqualified", "disqualify", "dissatisfied", "dissatisfy", "diversified", "diversify", "divvied", "divvy", "dizzied",
			"dizzy", "done", "do", "donned", "don", "donning", "don", "dotted", "dot", "dotting", "dot", "dove", "dive", "dragged", "drag", "dragging",
			"drag", "drank", "drink", "drawn", "draw", "dreamt", "dream", "drew", "draw", "dried", "dry", "dripped", "drip", "dripping", "drip", "driven",
			"drive", "dropped", "drop", "dropping", "drop", "drove", "drive", "drubbed", "drub", "drubbing", "drub", "drummed", "drum", "drumming", "drum",
			"drunk", "drink", "dubbed", "dub", "dubbing", "dub", "duelled", "duel", "duelling", "duel", "dug", "dig", "dwelt", "dwell", "dying", "die",
			"eaten", "eat", "eavesdropped", "eavesdrop", "eavesdropping", "eavesdrop", "electrified", "electrify", "embedded", "embed", "embedding",
			"embed", "embodied", "embody", "emitted", "emit", "emitting", "emit", "emptied", "empty", "enthralled", "enthral", "enthralling", "enthral",
			"envied", "envy", "equalled", "equal", "equalling", "equal", "equipped", "equip", "equipping", "equip", "excelled", "excel", "excelling",
			"excel", "exemplified", "exemplify", "expelled", "expel", "expelling", "expel", "extolled", "extol", "extolling", "extol", "fallen", "fall",
			"falsified", "falsify", "fancied", "fancy", "fanned", "fan", "fanning", "fan", "fed", "feed", "feed", "feed", "fell", "fall", "felt", "feel",
			"ferried", "ferry", "fitted", "fit", "fitting", "fit", "flagged", "flag", "flagging", "flag", "fled", "flee", "flew", "fly", "flipped", "flip",
			"flipping", "flip", "flitted", "flit", "flitting", "flit", "flopped", "flop", "flopping", "flop", "flown", "fly", "flung", "fling", "fogged",
			"fog", "fogging", "fog", "forbad", "forbid", "forbade", "forbid", "forbidden", "forbid", "forbidding", "forbid", "foregone", "forego",
			"foresaw", "foresee", "foreseen", "foresee", "foretold", "foretell", "forewent", "forego", "forgave", "forgive", "forgetting", "forget",
			"forgiven", "forgive", "forgone", "forgo", "forgot", "forget", "forgotten", "forget", "forsaken", "forsake", "forsook", "forsake", "fortified",
			"fortify", "forwent", "forgo", "fought", "fight", "found", "find", "fretted", "fret", "fretting", "fret", "fried", "fry", "frolicked", "frolic",
			"frolicking", "frolic", "froze", "freeze", "frozen", "freeze", "fuelled", "fuel", "fuelling", "fuel", "funnelled", "funnel", "funnelling",
			"funnel", "gapped", "gap", "gapping", "gap", "gassed", "gas", "gasses", "gas", "gassing", "gas", "gave", "give", "gelled", "gel", "gelling",
			"gel", "getting", "get", "girt", "gird", "given", "give", "glorified", "glorify", "glutted", "glut", "glutting", "glut", "gnawn", "gnaw",
			"gone", "go", "got", "get", "gotten", "get", "grabbed", "grab", "grabbing", "grab", "gratified", "gratify", "grew", "grow", "grinned", "grin",
			"grinning", "grin", "gripped", "grip", "gripping", "grip", "gript", "grip", "gritted", "grit", "gritting", "grit", "ground", "grind",
			"grovelled", "grovel", "grovelling", "grovel", "grown", "grow", "gummed", "gum", "gumming", "gum", "gunned", "gun", "gunning", "gun", "had",
			"have", "handicapped", "handicap", "handicapping", "handicap", "harried", "harry", "has", "have", "heard", "hear", "held", "hold", "hewn",
			"hew", "hid", "hide", "hidden", "hide", "hitting", "hit", "hobnobbed", "hobnob", "hobnobbing", "hobnob", "honied", "honey", "hopped", "hop",
			"hopping", "hop", "horrified", "horrify", "hove", "heave", "hugged", "hug", "hugging", "hug", "hummed", "hum", "humming", "hum", "hung", "hang",
			"hurried", "hurry", "identified", "identify", "impelled", "impel", "impelling", "impel", "implied", "imply", "incurred", "incur", "incurring",
			"incur", "indemnified", "indemnify", "inferred", "infer", "inferring", "infer", "initialled", "initial", "initialling", "initial",
			"intensified", "intensify", "interred", "inter", "interring", "inter", "interwove", "interweave", "interwoven", "interweave", "is", "be",
			"jagged", "jag", "jagging", "jag", "jammed", "jam", "jamming", "jam", "jetted", "jet", "jetting", "jet", "jimmied", "jimmy", "jogged", "jog",
			"jogging", "jog", "justified", "justify", "kept", "keep", "kidded", "kid", "kidding", "kid", "kidnapped", "kidnap", "kidnapping", "kidnap",
			"knelt", "kneel", "knew", "know", "knitted", "knit", "knitting", "knit", "knotted", "knot", "knotting", "knot", "known", "know", "labelled",
			"label", "labelling", "label", "laden", "lade", "lagged", "lag", "lagging", "lag", "laid", "lay", "lain", "lie", "lay", "lie", "leant", "lean",
			"leapfrogged", "leapfrog", "leapfrogging", "leapfrog", "leapt", "leap", "learnt", "learn", "led", "lead", "left", "leave", "lent", "lend",
			"letting", "let", "levelled", "level", "levelling", "level", "levied", "levy", "libelled", "libel", "libelling", "libel", "liquefied",
			"liquefy", "lit", "light", "lobbed", "lob", "lobbied", "lobby", "lobbing", "lob", "logged", "log", "logging", "log", "lost", "lose", "lugged",
			"lug", "lugging", "lug", "lying", "lie", "made", "make", "magnified", "magnify", "manned", "man", "manning", "man", "mapped", "map", "mapping",
			"map", "marred", "mar", "married", "marry", "marring", "mar", "marshalled", "marshal", "marshalling", "marshal", "marvelled", "marvel",
			"marvelling", "marvel", "meant", "mean", "met", "meet", "mimicked", "mimic", "mimicking", "mimic", "misapplied", "misapply", "misled",
			"mislead", "misspelt", "misspell", "mistaken", "mistake", "mistook", "mistake", "misunderstood", "misunderstand", "modelled", "model",
			"modelling", "model", "modified", "modify", "mollified", "mollify", "molten", "melt", "mopped", "mop", "mopping", "mop", "mown", "mow",
			"multiplied", "multiply", "mummified", "mummify", "mystified", "mystify", "nabbed", "nab", "nabbing", "nab", "nagged", "nag", "nagging", "nag",
			"napped", "nap", "napping", "nap", "netted", "net", "netting", "net", "nipped", "nip", "nipping", "nip", "nodded", "nod", "nodding", "nod",
			"notified", "notify", "nullified", "nullify", "occupied", "occupy", "occurred", "occur", "occurring", "occur", "offsetting", "offset",
			"omitted", "omit", "omitting", "omit", "ossified", "ossify", "outbidden", "outbid", "outbidding", "outbid", "outdid", "outdo", "outdone",
			"outdo", "outfitted", "outfit", "outfitting", "outfit", "outgrew", "outgrow", "outgrown", "outgrow", "outputted", "output", "outputting",
			"output", "outran", "outrun", "outrunning", "outrun", "outshone", "outshine", "outsold", "outsell", "outstripped", "outstrip", "outstripping",
			"outstrip", "outwitted", "outwit", "outwitting", "outwit", "overcame", "overcome", "overdid", "overdo", "overdone", "overdo", "overdrawn",
			"overdraw", "overdrew", "overdraw", "overflown", "overflow", "overheard", "overhear", "overhung", "overhang", "overlaid", "overlay",
			"overlapped", "overlap", "overlapping", "overlap", "overpaid", "overpay", "overridden", "override", "overrode", "override",
			"oversaw", "oversee", "overseen", "oversee", "oversimplified", "oversimplify", "overspent", "overspend", "overstepped", "overstep",
			"overstepping", "overstep", "overtaken", "overtake", "overthrew", "overthrow", "overthrown", "overthrow", "overtook", "overtake", "pacified",
			"pacify", "padded", "pad", "padding", "pad", "paid", "pay", "panicked", "panic", "panicking", "panic", "panned", "pan", "panning", "pan",
			"parallelled", "parallel", "parallelling", "parallel", "parcelled", "parcel", "parcelling", "parcel", "parodied", "parody", "parried", "parry",
			"partaken", "partake", "partook", "partake", "patrolled", "patrol", "patrolling", "patrol", "patted", "pat", "patting", "pat", "pedalled",
			"pedal", "pedalling", "pedal", "pegged", "peg", "pegging", "peg", "pencilled", "pencil", "pencilling", "pencil", "penned", "pen", "penning",
			"pen", "pent", "pen", "permitted", "permit", "permitting", "permit", "personified", "personify", "petrified", "petrify", "petted", "pet",
			"petting", "pet", "photocopied", "photocopy", "pilloried", "pillory", "pinned", "pin", "pinning", "pin", "pitied", "pity", "pitted", "pit",
			"pitting", "pit", "planned", "plan", "planning", "plan", "pled", "plead", "plied", "ply", "plodded", "plod", "plodding", "plod", "plopped",
			"plop", "plopping", "plop", "plotted", "plot", "plotting", "plot", "plugged", "plug", "plugging", "plug", "popped", "pop", "popping", "pop",
			"potted", "pot", "potting", "pot", "preferred", "prefer", "preferring", "prefer", "preoccupied", "preoccupy", "prepaid", "prepay", "pried",
			"pry", "prodded", "prod", "prodding", "prod", "programmed", "program", "programmes", "program", "programming", "program", "propelled", "propel",
			"propelling", "propel", "prophesied", "prophesy", "propped", "prop", "propping", "prop", "proven", "prove", "pummelled", "pummel", "pummelling",
			"pummel", "purified", "purify", "putting", "put", "qualified", "qualify", "quantified", "quantify", "quarrelled", "quarrel", "quarrelling",
			"quarrel", "quitted", "quit", "quitting", "quit", "quizzed", "quiz", "quizzes", "quiz", "quizzing", "quiz", "rallied", "rally", "rammed", "ram",
			"ramming", "ram", "ran", "run", "rang", "ring", "rapped", "rap", "rapping", "rap", "rarefied", "rarefy", "ratified", "ratify", "ratted", "rat",
			"ratting", "rat", "rebelled", "rebel", "rebelling", "rebel", "rebuilt", "rebuild", "rebutted", "rebut", "rebutting", "rebut", "reclassified",
			"reclassify", "rectified", "rectify", "recurred", "recur", "recurring", "recur", "redid", "redo", "redone", "redo", "referred", "refer",
			"referring", "refer", "refitted", "refit", "refitting", "refit", "refuelled", "refuel", "refuelling", "refuel", "regretted", "regret",
			"regretting", "regret", "reheard", "rehear", "relied", "rely", "remade", "remake", "remarried", "remarry", "remitted", "remit", "remitting",
			"remit", "repaid", "repay", "repelled", "repel", "repelling", "repel", "replied", "reply", "resetting", "reset", "retaken", "retake",
			"rethought", "rethink", "retook", "retake", "retried", "retry", "retrofitted", "retrofit", "retrofitting", "retrofit", "revelled", "revel",
			"revelling", "revel", "revved", "rev", "revving", "rev", "rewritten", "rewrite", "rewrote", "rewrite", "ricochetted", "ricochet",
			"ricochetting", "ricochet", "ridded", "rid", "ridden", "ride", "ridding", "rid", "rigged", "rig", "rigging", "rig", "ripped", "rip", "ripping",
			"rip", "risen", "rise", "rivalled", "rival", "rivalling", "rival", "robbed", "rob", "robbing", "rob", "rode", "ride", "rose", "rise", "rotted",
			"rot", "rotting", "rot", "rubbed", "rub", "rubbing", "rub", "rung", "ring", "running", "run", "sagged", "sag", "sagging", "sag", "said", "say",
			"sallied", "sally", "sang", "sing", "sank", "sink", "sapped", "sap", "sapping", "sap", "sat", "sit", "satisfied", "satisfy", "savvied", "savvy",
			"saw", "see", "scanned", "scan", "scanning", "scan", "scrapped", "scrap", "scrapping", "scrap", "scrubbed", "scrub", "scrubbing", "scrub",
			"scurried", "scurry", "seed", "seed", "seen", "see", "sent", "send", "setting", "set", "sewn", "sew", "shaken", "shake", "shaven", "shave",
			"shed", "shed", "shedding", "shed", "shied", "shy", "shimmed", "shim", "shimmied", "shimmy", "shimming", "shim", "shipped", "ship", "shipping",
			"ship", "shone", "shine", "shook", "shake", "shopped", "shop", "shopping", "shop", "shot", "shoot", "shovelled", "shovel", "shovelling",
			"shovel", "shown", "show", "shrank", "shrink", "shredded", "shred", "shredding", "shred", "shrivelled", "shrivel", "shrivelling", "shrivel",
			"shrugged", "shrug", "shrugging", "shrug", "shrunk", "shrink", "shrunken", "shrink", "shunned", "shun", "shunning", "shun", "shutting", "shut",
			"sicked", "sic", "sicking", "sic", "sidestepped", "sidestep", "sidestepping", "sidestep", "signalled", "signal", "signalling", "signal",
			"signified", "signify", "simplified", "simplify", "singing", "sing", "sinned", "sin", "sinning", "sin", "sitting", "sit", "ski'd", "ski",
			"skidded", "skid", "skidding", "skid", "skimmed", "skim", "skimming", "skim", "skipped", "skip", "skipping", "skip", "slain", "slay", "slammed",
			"slam", "slamming", "slam", "slapped", "slap", "slapping", "slap", "sledding", "sled", "slept", "sleep", "slew", "slay", "slid", "slide",
			"slidden", "slide", "slipped", "slip", "slipping", "slip", "slitting", "slit", "slogged", "slog", "slogging", "slog", "slopped", "slop",
			"slopping", "slop", "slugged", "slug", "slugging", "slug", "slung", "sling", "slurred", "slur", "slurring", "slur", "smelt", "smell", "snagged",
			"snag", "snagging", "snag", "snapped", "snap", "snapping", "snap", "snipped", "snip", "snipping", "snip", "snubbed", "snub", "snubbing", "snub",
			"snuck", "sneak", "sobbed", "sob", "sobbing", "sob", "sold", "sell", "solidified", "solidify", "sought", "seek", "sown", "sow", "spanned",
			"span", "spanning", "span", "spat", "spit", "specified", "specify", "sped", "speed", "spelt", "spell", "spent", "spend", "spied", "spy",
			"spilt", "spill", "spinning", "spin", "spiralled", "spiral", "spiralling", "spiral", "spitted", "spit", "spitting", "spit", "splitting",
			"split", "spoilt", "spoil", "spoke", "speak", "spoken", "speak", "spotlit", "spotlight", "spotted", "spot", "spotting", "spot", "sprang",
			"spring", "sprung", "spring", "spun", "spin", "spurred", "spur", "spurring", "spur", "squatted", "squat", "squatting", "squat", "stank",
			"stink", "starred", "star", "starring", "star", "stemmed", "stem", "stemming", "stem", "stepped", "step", "stepping", "step", "stirred", "stir",
			"stirring", "stir", "stole", "steal", "stolen", "steal", "stood", "stand", "stopped", "stop", "stopping", "stop", "stove", "stave", "strapped",
			"strap", "strapping", "strap", "stratified", "stratify", "stridden", "stride", "stripped", "strip", "stripping", "strip", "striven", "strive",
			"strode", "stride", "strove", "strive", "struck", "strike", "strung", "string", "stubbed", "stub", "stubbing", "stub", "stuck", "stick",
			"studied", "study", "stung", "sting", "stunk", "stink", "stunned", "stun", "stunning", "stun", "stymying", "stymie", "subletting", "sublet",
			"submitted", "submit", "submitting", "submit", "summed", "sum", "summing", "sum", "sung", "sing", "sunk", "sink", "sunken", "sink", "sunned",
			"sun", "sunning", "sun", "supplied", "supply", "swabbed", "swab", "swabbing", "swab", "swam", "swim", "swapped", "swap", "swapping", "swap",
			"swept", "sweep", "swimming", "swim", "swivelled", "swivel", "swivelling", "swivel", "swollen", "swell", "swopped", "swap", "swopping", "swap",
			"swops", "swap", "swore", "swear", "sworn", "swear", "swum", "swim", "swung", "swing", "tagged", "tag", "tagging", "tag", "taken", "take",
			"tallied", "tally", "tapped", "tap", "tapping", "tap", "tarried", "tarry", "taught", "teach", "taxying", "taxi", "terrified", "terrify",
			"testified", "testify", "thinned", "thin", "thinning", "thin", "thought", "think", "threw", "throw", "thriven", "thrive", "throbbed", "throb",
			"throbbing", "throb", "throve", "thrive", "thrown", "throw", "thudded", "thud", "thudding", "thud", "tipped", "tip", "tipping", "tip", "told",
			"tell", "took", "take", "topped", "top", "topping", "top", "tore", "tear", "torn", "tear", "totalled", "total", "totalling", "total",
			"trafficked", "traffic", "trafficking", "traffic", "trameled", "trammel", "trameling", "trammel", "tramelled", "trammel", "tramelling",
			"trammel", "tramels", "trammel", "transferred", "transfer", "transferring", "transfer", "transmitted", "transmit", "transmitting", "transmit",
			"trapped", "trap", "trapping", "trap", "travelled", "travel", "travelling", "travel", "trekked", "trek", "trekking", "trek", "tried", "try",
			"trimmed", "trim", "trimming", "trim", "tripped", "trip", "tripping", "trip", "trod", "tread", "trodden", "tread", "trotted", "trot",
			"trotting", "trot", "tugged", "tug", "tugging", "tug", "tunnelled", "tunnel", "tunnelling", "tunnel", "tying", "tie", "typified", "typify",
			"undercutting", "undercut", "undergone", "undergo", "underlain", "underlie", "underlay", "underlie", "underlying", "underlie", "underpinned",
			"underpin", "underpinning", "underpin", "understood", "understand", "undertaken", "undertake", "undertook", "undertake", "underwent", "undergo",
			"underwritten", "underwrite", "underwrote", "underwrite", "undid", "undo", "undone", "undo", "unified", "unify", "unravelled", "unravel",
			"unravelling", "unravel", "unsteadied", "unsteady", "untying", "untie", "unwound", "unwind", "upheld", "uphold", "upsetting", "upset", "varied",
			"vary", "verified", "verify", "vilified", "vilify", "wadded", "wad", "wadding", "wad", "wagged", "wag", "wagging", "wag", "was", "be",
			"wearied", "weary", "wedded", "wed", "wedding", "wed", "weed", "weed", "went", "go", "wept", "weep", "were", "be", "wetted", "wet", "wetting",
			"wet", "whetted", "whet", "whetting", "whet", "whipped", "whip", "whipping", "whip", "whizzed", "whiz", "whizzes", "whiz", "whizzing", "whiz",
			"winning", "win", "withdrawn", "withdraw", "withdrew", "withdraw", "withheld", "withhold", "withstood", "withstand", "woke", "wake", "woken",
			"wake", "won", "win", "wore", "wear", "worn", "wear", "worried", "worry", "worshipped", "worship", "worshipping", "worship", "wound", "wind",
			"wove", "weave", "woven", "weave", "wrapped", "wrap", "wrapping", "wrap", "written", "write", "wrote", "write", "wrought", "work", "wrung",
			"wring", "yodelled", "yodel", "yodelling", "yodel", "zapped", "zap", "zapping", "zap", "zigzagged", "zigzag", "zigzagging", "zigzag", "zipped",
			"zip", "zipping", "zip"
	};

	public static final String[] IRREG_VERBS_NOLEX = {
			"abutted", "abut", "abutting", "abut", "ad-libbed", "ad-lib", "ad-libbing", "ad-lib", "aerified", "aerify", "air-dried", "air-dry",
			"airdropped", "airdrop", "airdropping", "airdrop", "appalled", "appal", "appalling", "appal", "averred", "aver", "averring", "aver", "baby-sat",
			"baby-sit", "baby-sitting", "baby-sit", "back-pedalled", "back-pedal", "back-pedalling", "back-pedal", "backslid", "backslide", "backslidden",
			"backslide", "befogged", "befog", "befogging", "befog", "begirt", "begird", "bejewelled", "bejewel", "bejewelling", "bejewel", "belly-flopped",
			"belly-flop", "belly-flopping", "belly-flop", "blabbed", "blab", "blabbing", "blab", "bobbed", "bob", "bobbing", "bob", "bogged-down",
			"bog-down", "bogged_down", "bog_down", "bogging-down", "bog-down", "bogging_down", "bog_down", "bogs-down", "bog-down", "bogs_down", "bog_down",
			"booby-trapped", "booby-trap", "booby-trapping", "booby-trap", "bottle-fed", "bottle-feed", "breast-fed", "breast-feed", "brutified", "brutify",
			"bullshitted", "bullshit", "bullshitting", "bullshit", "bullwhipped", "bullwhip", "bullwhipping", "bullwhip", "caddies", "caddie", "caddying",
			"caddie", "carolled", "carol", "carolling", "carol", "catnapped", "catnap", "catnapping", "catnap", "citified", "citify", "cleft", "cleave",
			"clopped", "clop", "clopping", "clop", "clove", "cleave", "cloven", "cleave", "co-opted", "coopt", "co-opting", "coopt", "co-opts", "coopts",
			"co-starred", "co-star", "co-starring", "co-star", "coiffed", "coif", "coiffing", "coif", "court-martialled", "court-martial",
			"court-martialling", "court-martial", "crossbred", "crossbreed", "crosscutting", "crosscut", "curtsied", "curtsy", "dabbed", "dab", "dabbing",
			"dab", "dandified", "dandify", "debarred", "debar", "debarring", "debar", "debugged", "debug", "debugging", "debug", "decalcified", "decalcify",
			"declassified", "declassify", "decontrolled", "decontrol", "deep-fried", "deep-fry", "dehumidified", "dehumidify", "deified", "deify",
			"demystified", "demystify", "disbarred", "disbar", "disbarring", "disbar", "disembodied", "disembody", "disembowelled", "disembowel",
			"disembowelling", "disembowel", "disenthralled", "disenthral", "disenthralling", "disenthral", "disenthralls", "disenthral", "disenthrals",
			"disenthrall", "disinterred", "disinter", "disinterring", "disinter", "distilled", "distil", "distilling", "distil", "eddied", "eddy",
			"edified", "edify", "ego-tripped", "ego-trip", "ego-tripping", "ego-trip", "empanelled", "empanel", "empanelling", "empanel", "emulsified",
			"emulsify", "entrapped", "entrap", "entrapping", "entrap", "fibbed", "fib", "fibbing", "fib", "filled_up", "fill_up", "flip-flopped",
			"flip-flop", "flip-flopping", "flip-flop", "flogged", "flog", "flogging", "flog", "foreran", "forerun", "forerunning", "forerun", "foxtrotted",
			"foxtrot", "foxtrotting", "foxtrot", "freeze-dried", "freeze-dry", "frigged", "frig", "frigging", "frig", "fritted", "frit", "fritting", "frit",
			"fulfilled", "fulfil", "fulfilling", "fulfil", "gambolled", "gambol", "gambolling", "gambol", "gasified", "gasify", "gelt", "geld", "gets_lost",
			"get_lost", "gets_started", "get_started", "getting_lost", "get_lost", "getting_started", "get_started", "ghostwritten", "ghostwrite",
			"ghostwrote", "ghostwrite", "giftwrapped", "giftwrap", "giftwrapping", "giftwrap", "gilt", "gild", "gipped", "gip", "gipping", "gip", "glommed",
			"glom", "glomming", "glom", "goes_deep", "go_deep", "going_deep", "go_deep", "gone_deep", "go_deep", "goose-stepped", "goose-step",
			"goose-stepping", "goose-step", "got_lost", "get_lost", "got_started", "get_started", "gotten_lost", "get_lost", "gypped", "gyp", "gypping",
			"gyp", "had_a_feeling", "have_a_feeling", "had_left", "have_left", "had_the_feeling", "have_the_feeling", "hand-knitted", "hand-knit",
			"hand-knitting", "hand-knit", "handfed", "handfeed", "has_a_feeling", "have_a_feeling", "has_left", "have_left", "has_the_feeling",
			"have_the_feeling", "having_a_feeling", "have_a_feeling", "having_left", "have_left", "having_the_feeling", "have_the_feeling", "high-hatted",
			"high-hat", "high-hatting", "high-hat", "hogtying", "hogtie", "horsewhipped", "horsewhip", "horsewhipping", "horsewhip", "humidified",
			"humidify", "hypertrophied", "hypertrophy", "inbred", "inbreed", "installed", "instal", "installing", "instal", "interbred", "interbreed",
			"intercutting", "intercut", "interlaid", "interlay", "interlapped", "interlap", "intermarried", "intermarry", "jellified", "jellify", "jibbed",
			"jib", "jibbing", "jib", "jitterbugged", "jitterbug", "jitterbugging", "jitterbug", "joined_forces", "join_forces", "joining_battle",
			"join_battle", "joining_forces", "join_forces", "joins_battle", "join_battle", "joins_forces", "join_forces", "joy-ridden", "joy-ride",
			"joy-rode", "joy-ride", "jumped_off", "jump_off", "jumping_off", "jump_off", "jumps_off", "jump_off", "jutted", "jut", "jutting", "jut",
			"knapped", "knap", "knapping", "knap", "ko'd", "ko", "ko'ing", "ko", "ko's", "ko", "lallygagged", "lallygag", "lallygagging", "lallygag",
			"leaves_undone", "leave_undone", "leaving_undone", "leave_undone", "left_undone", "leave_undone", "lignified", "lignify", "liquified",
			"liquify", "looked_towards", "look_towards", "looking_towards", "look_towards", "looks_towards", "look_towards", "machine-gunned",
			"machine-gun", "machine-gunning", "machine-gun", "minified", "minify", "miscarried", "miscarry", "misdealt", "misdeal", "misgave", "misgive",
			"misgiven", "misgive", "mislaid", "mislay", "mispled", "misplead", "misspent", "misspend", "mortified", "mortify", "objectified", "objectify",
			"outfought", "outfight", "outridden", "outride", "outrode", "outride", "outshot", "outshoot", "outthought", "outthink", "overbidden", "overbid",
			"overbidding", "overbid", "overblew", "overblow", "overblown", "overblow", "overflew", "overfly", "overgrew", "overgrow", "overgrown",
			"overgrow", "overshot", "overshoot", "overslept", "oversleep", "overwritten", "overwrite", "overwrote", "overwrite", "pinch-hitting",
			"pinch-hit", "pistol-whipped", "pistol-whip", "pistol-whipping", "pistol-whip", "played_a_part", "play_a_part", "playing_a_part", "play_a_part",
			"plays_a_part", "play_a_part", "pommelled", "pommel", "pommelling", "pommel", "prettified", "prettify", "putrefied", "putrefy", "quickstepped",
			"quickstep", "quickstepping", "quickstep", "rappelled", "rappel", "rappelling", "rappel", "recapped", "recap", "recapping", "recap",
			"recommitted", "recommit", "recommitting", "recommit", "reified", "reify", "rent", "rend", "repotted", "repot", "repotting", "repot",
			"retransmitted", "retransmit", "retransmitting", "retransmit", "reunified", "reunify", "rewound", "rewind", "sanctified", "sanctify",
			"sandbagged", "sandbag", "sandbagging", "sandbag", "scarified", "scarify", "scatted", "scat", "scrammed", "scram", "scramming", "scram",
			"scrummed", "scrum", "scrumming", "scrum", "shagged", "shag", "shagging", "shag", "shaken_hands", "shake_hands", "shakes_hands", "shake_hands",
			"shaking_hands", "shake_hands", "sharecropped", "sharecrop", "sharecropping", "sharecrop", "shellacked", "shellac", "shellacking", "shellac",
			"shook_hands", "shake_hands", "shrink-wrapped", "shrink-wrap", "shrink-wrapping", "shrink-wrap", "sideslipped", "sideslip", "sideslipping",
			"sideslip", "sightsaw", "sightsee", "sightseen", "sightsee", "skinny-dipped", "skinny-dip", "skinny-dipping", "skinny-dip", "skydove",
			"skydive", "slunk", "slink", "smit", "smite", "smitten", "smite", "smote", "smite", "snivelled", "snivel", "snivelling", "snivel", "snogged",
			"snog", "snogging", "snog", "soft-pedalled", "soft-pedal", "soft-pedalling", "soft-pedal", "sparred", "spar", "sparring", "spar", "speechified",
			"speechify", "spellbound", "spellbind", "spin-dried", "spin-dry", "spoon-fed", "spoon-feed", "stems_from", "stem_from", "stencilled", "stencil",
			"stencilling", "stencil", "strewn", "strew", "strummed", "strum", "strumming", "strum", "stultified", "stultify", "stupefied", "stupefy",
			"subjectified", "subjectify", "subtotalled", "subtotal", "subtotalling", "subtotal", "sullied", "sully", "supped", "sup", "supping", "sup",
			"syllabified", "syllabify", "taken_a_side", "take_a_side", "taken_pains", "take_pains", "taken_steps", "take_steps", "takes_a_side",
			"take_a_side", "takes_pains", "take_pains", "takes_steps", "take_steps", "taking_a_side", "take_a_side", "taking_pains", "take_pains",
			"taking_steps", "take_steps", "talcked", "talc", "talcking", "talc", "threw_out", "throw_out", "throwing_out", "throw_out", "thrown_out",
			"throw_out", "throws_out", "throw_out", "thrummed", "thrum", "thrumming", "thrum", "took_a_side", "take_a_side", "took_pains", "take_pains",
			"took_steps", "take_steps", "trammed", "tram", "tramming", "tram", "transfixt", "transfix", "transmogrified", "transmogrify", "trepanned",
			"trepan", "trepanning", "trepan", "typesetting", "typeset", "typewritten", "typewrite", "typewrote", "typewrite", "uglified", "uglify",
			"unbound", "unbind", "unclad", "unclothe", "underbidding", "underbid", "underfed", "underfeed", "underpaid", "underpay", "undersold",
			"undersell", "understudied", "understudy", "unfroze", "unfreeze", "unfrozen", "unfreeze", "unlearnt", "unlearn", "unmade", "unmake", "unmanned",
			"unman", "unmanning", "unman", "unpinned", "unpin", "unpinning", "unpin", "unplugged", "unplug", "unplugging", "unplug", "unslung", "unsling",
			"unstrung", "unstring", "unstuck", "unstick", "unwrapped", "unwrap", "unwrapping", "unwrap", "unzipped", "unzip", "unzipping", "unzip",
			"uppercutting", "uppercut", "verbified", "verbify", "versified", "versify", "vivified", "vivify", "vying", "vie", "waylaid", "waylay",
			"went_deep", "go_deep", "whinnied", "whinny", "whirred", "whir", "whirring", "whir", "window-shopped", "window-shop", "window-shopping",
			"window-shop", "yakked", "yak", "yakking", "yak", "yapped", "yap", "yapping", "yap"
	};
	private static final String CONS = "[bcdfghjklmnpqrstvwxyz]";
	private static final String ANY_STEM = "^((\\w+)(-\\w+)*)(\\s((\\w+)(-\\w+)*))*$";
	private static final String VERBAL_PREFIX = "((be|with|pre|un|over|re|mis|under|out|up|fore|for|counter|co|sub)(-?))";
	private static final String[] MODALS = { "shall", "would", "may", "might", "ought", "should" };
	private static final String[] IRREG_PAST_PART = { "done", "gone", "abode", "been", "begotten", "begun", "bent", "bid",
			"bidden", "bled", "born", "bought", "brought", "built", "caught", "clad", "chlung", "could", "crept",
			"dove", "drunk", "dug", "dwelt", "fed", "felt", "fled", "flung", "fought", "found", "ground", "had",
			"held", "hung", "hurt", "kept", "knelt", "laid", "lain", "led", "left", "lent", "lit", "lost", "made",
			"met", "mown", "paid", "pled", "relaid", "rent", "rung", "said", "sat", "sent", "shod", "shot", "slain",
			"slept", "slid", "smelt", "sold", "sought", "spat", "sped", "spelt", "spent", "split", "spolit", "sprung",
			"spun", "stood", "stuck", "struck", "stung", "stunk", "sung", "sunk", "swept", "sworn", "swum", "swung",
			"taight", "thought", "told", "torn", "undergone", "understood", "wept", "woken", "won", "worn", "wound",
			"wrung" };

	private static final RE[] ING_FORM_RULES = {
			new RE(CONS + "ie$", 2, "ying", 1),
			new RE("[^ie]e$", 1, "ing", 1),
			new RE("^bog-down$", 5, "ging-down", 0),
			new RE("^chivy$", 1, "vying", 0),
			new RE("^trek$", 1, "cking", 0),
			new RE("^bring$", 0, "ing", 0),
			new RE("^be$", 0, "ing", 0),
			new RE("^age$", 1, "ing", 0),
			new RE("(ibe)$", 1, "ing", 0)
	};

	private static final RE[] PAST_PART_RULES = {

			new RE(CONS + "y$", 1, "ied", 1), new RE("^" + VERBAL_PREFIX + "?(bring)$", 3, "ought", 0),
			new RE("^" + VERBAL_PREFIX + "?(take|rise|strew|blow|draw|drive|know|give|"
					+ "arise|gnaw|grave|grow|hew|know|mow|see|sew|throw|prove|saw|quartersaw|"
					+ "partake|sake|shake|shew|show|shrive|sightsee|strew|strive)$", 0, "n", 0),
			new RE("^" + VERBAL_PREFIX + "?[gd]o$", 0, "ne", 1), new RE("^(beat|eat|be|fall)$", 0, "en", 0),
			new RE("^(have)$", 2, "d", 0), new RE("^" + VERBAL_PREFIX + "?bid$", 0, "den", 0),
			new RE("^" + VERBAL_PREFIX + "?[lps]ay$", 1, "id", 1), new RE("^behave$", 0, "d", 0),
			new RE("^" + VERBAL_PREFIX + "?have$", 2, "d", 1), new RE("(sink|slink|drink|shrink|stink)$", 3, "unk", 0),
			new RE("(([sfc][twlp]?r?|w?r)ing|hang)$", 3, "ung", 0),
			new RE("^" + VERBAL_PREFIX + "?(shear|swear|bear|wear|tear)$", 3, "orn", 0),
			new RE("^" + VERBAL_PREFIX + "?(bend|spend|send|lend)$", 1, "t", 0),
			new RE("^" + VERBAL_PREFIX + "?(weep|sleep|sweep|creep|keep$)$", 2, "pt", 0),
			new RE("^" + VERBAL_PREFIX + "?(sell|tell)$", 3, "old", 0), new RE("^(outfight|beseech)$", 4, "ought", 0),
			new RE("^bethink$", 3, "ought", 0), new RE("^buy$", 2, "ought", 0), new RE("^aby$", 1, "ought", 0),
			new RE("^tarmac", 0, "ked", 0), new RE("^abide$", 3, "ode", 0),
			new RE("^" + VERBAL_PREFIX + "?(speak|(a?)wake|break)$", 3, "oken", 0), new RE("^backbite$", 1, "ten", 0),
			new RE("^backslide$", 1, "den", 0), new RE("^become$", 3, "ame", 0), new RE("^begird$", 3, "irt", 0),
			new RE("^outlie$", 2, "ay", 0), new RE("^rebind$", 3, "ound", 0), new RE("^relay$", 2, "aid", 0),
			new RE("^shit$", 3, "hat", 0), new RE("^bereave$", 4, "eft", 0), new RE("^foreswear$", 3, "ore", 0),
			new RE("^overfly$", 1, "own", 0), new RE("^beget$", 2, "otten", 0), new RE("^begin$", 3, "gun", 0),
			new RE("^bestride$", 1, "den", 0), new RE("^bite$", 1, "ten", 0), new RE("^bleed$", 4, "led", 0),
			new RE("^bog-down$", 5, "ged-down", 0), new RE("^bind$", 3, "ound", 0), new RE("^(.*)feed$", 4, "fed", 0),
			new RE("^breed$", 4, "red", 0), new RE("^brei", 0, "d", 0), new RE("^bring$", 3, "ought", 0),
			new RE("^build$", 1, "t", 0), new RE("^come", 0), new RE("^catch$", 3, "ught", 0),
			new RE("^chivy$", 1, "vied", 0), new RE("^choose$", 3, "sen", 0), new RE("^cleave$", 4, "oven", 0),
			new RE("^crossbreed$", 4, "red", 0), new RE("^deal", 0, "t", 0), new RE("^dow$", 1, "ught", 0),
			new RE("^dream", 0, "t", 0), new RE("^dig$", 3, "dug", 0), new RE("^dwell$", 2, "lt", 0),
			new RE("^enwind$", 3, "ound", 0), new RE("^feel$", 3, "elt", 0), new RE("^flee$", 2, "ed", 0),
			new RE("^floodlight$", 5, "lit", 0), new RE("^fly$", 1, "own", 0), new RE("^forbear$", 3, "orne", 0),
			new RE("^forerun$", 3, "ran", 0), new RE("^forget$", 2, "otten", 0), new RE("^fight$", 4, "ought", 0),
			new RE("^find$", 3, "ound", 0), new RE("^freeze$", 4, "ozen", 0), new RE("^gainsay$", 2, "aid", 0),
			new RE("^gin$", 3, "gan", 0), new RE("^gen-up$", 3, "ned-up", 0), new RE("^ghostwrite$", 1, "ten", 0),
			new RE("^get$", 2, "otten", 0), new RE("^grind$", 3, "ound", 0), new RE("^hacksaw", 0, "n", 0),
			new RE("^hear", 0, "d", 0), new RE("^hold$", 3, "eld", 0), new RE("^hide$", 1, "den", 0),
			new RE("^honey$", 2, "ied", 0), new RE("^inbreed$", 4, "red", 0), new RE("^indwell$", 3, "elt", 0),
			new RE("^interbreed$", 4, "red", 0), new RE("^interweave$", 4, "oven", 0),
			new RE("^inweave$", 4, "oven", 0), new RE("^ken$", 2, "ent", 0), new RE("^kneel$", 3, "elt", 0),
			new RE("^lie$", 2, "ain", 0), new RE("^leap$", 0, "t", 0), new RE("^learn$", 0, "t", 0),
			new RE("^lead$", 4, "led", 0), new RE("^leave$", 4, "eft", 0), new RE("^light$", 5, "lit", 0),
			new RE("^lose$", 3, "ost", 0), new RE("^make$", 3, "ade", 0), new RE("^mean", 0, "t", 0),
			new RE("^meet$", 4, "met", 0), new RE("^misbecome$", 3, "ame", 0), new RE("^misdeal$", 2, "alt", 0),
			new RE("^mishear$", 1, "d", 0), new RE("^mislead$", 4, "led", 0), new RE("^misunderstand$", 3, "ood", 0),
			new RE("^outbreed$", 4, "red", 0), new RE("^outrun$", 3, "ran", 0), new RE("^outride$", 1, "den", 0),
			new RE("^outshine$", 3, "one", 0), new RE("^outshoot$", 4, "hot", 0), new RE("^outstand$", 3, "ood", 0),
			new RE("^outthink$", 3, "ought", 0), new RE("^outgo$", 2, "went", 0), new RE("^overbear$", 3, "orne", 0),
			new RE("^overbuild$", 3, "ilt", 0), new RE("^overcome$", 3, "ame", 0), new RE("^overfly$", 2, "lew", 0),
			new RE("^overhear$", 2, "ard", 0), new RE("^overlie$", 2, "ain", 0), new RE("^overrun$", 3, "ran", 0),
			new RE("^override$", 1, "den", 0), new RE("^overshoot$", 4, "hot", 0), new RE("^overwind$", 3, "ound", 0),
			new RE("^overwrite$", 1, "ten", 0), new RE("^plead$", 2, "d", 0), new RE("^rebuild$", 3, "ilt", 0),
			new RE("^red$", 3, "red", 0), new RE("^redo$", 1, "one", 0), new RE("^remake$", 3, "ade", 0),
			new RE("^resit$", 3, "sat", 0), new RE("^rethink$", 3, "ought", 0), new RE("^rewind$", 3, "ound", 0),
			new RE("^rewrite$", 1, "ten", 0), new RE("^ride$", 1, "den", 0), new RE("^reeve$", 4, "ove", 0),
			new RE("^sit$", 3, "sat", 0), new RE("^shoe$", 3, "hod", 0), new RE("^shine$", 3, "one", 0),
			new RE("^shoot$", 4, "hot", 0), new RE("^ski$", 1, "i'd", 0), new RE("^slide$", 1, "den", 0),
			new RE("^smite$", 1, "ten", 0), new RE("^seek$", 3, "ought", 0), new RE("^spit$", 3, "pat", 0),
			new RE("^speed$", 4, "ped", 0), new RE("^spellbind$", 3, "ound", 0), new RE("^spoil$", 2, "ilt", 0),
			new RE("^spotlight$", 5, "lit", 0), new RE("^spin$", 3, "pun", 0), new RE("^steal$", 3, "olen", 0),
			new RE("^stand$", 3, "ood", 0), new RE("^stave$", 3, "ove", 0), new RE("^stride$", 1, "den", 0),
			new RE("^strike$", 3, "uck", 0), new RE("^stick$", 3, "uck", 0), new RE("^swell$", 3, "ollen", 0),
			new RE("^swim$", 3, "wum", 0), new RE("^teach$", 4, "aught", 0), new RE("^think$", 3, "ought", 0),
			new RE("^tread$", 3, "odden", 0), new RE("^typewrite$", 1, "ten", 0), new RE("^unbind$", 3, "ound", 0),
			new RE("^underbuy$", 2, "ought", 0), new RE("^undergird$", 3, "irt", 0), new RE("^undergo$", 1, "one", 0),
			new RE("^underlie$", 2, "ain", 0), new RE("^undershoot$", 4, "hot", 0), new RE("^understand$", 3, "ood", 0),
			new RE("^unfreeze$", 4, "ozen", 0), new RE("^unlearn", 0, "t", 0), new RE("^unmake$", 3, "ade", 0),
			new RE("^unreeve$", 4, "ove", 0), new RE("^unstick$", 3, "uck", 0), new RE("^unteach$", 4, "aught", 0),
			new RE("^unthink$", 3, "ought", 0), new RE("^untread$", 3, "odden", 0), new RE("^unwind$", 3, "ound", 0),
			new RE("^upbuild$", 1, "t", 0), new RE("^uphold$", 3, "eld", 0), new RE("^upheave$", 4, "ove", 0),
			new RE("^waylay$", 2, "ain", 0), new RE("^whipsaw$", 2, "awn", 0), new RE("^withhold$", 3, "eld", 0),
			new RE("^withstand$", 3, "ood", 0), new RE("^win$", 3, "won", 0), new RE("^wind$", 3, "ound", 0),
			new RE("^weave$", 4, "oven", 0), new RE("^write$", 1, "ten", 0), new RE("^trek$", 1, "cked", 0),
			new RE("^ko$", 1, "o'd", 0), new RE("^win$", 2, "on", 0),

			new RE("e$", 0, "d", 1),

			// Null past forms
			new RE("^" + VERBAL_PREFIX
					+ "?(cast|thrust|typeset|cut|bid|upset|wet|bet|cut|hit|hurt|inset|let|cost|burst|beat|beset|set|upset|hit|offset|put|quit|"
					+ "wed|typeset|wed|spread|split|slit|read|run|rerun|shut|shed)$", 0) };

	private static final RE[] PAST_RULES = { new RE("^(reduce)$", 0, "d", 0),
			new RE("^" + VERBAL_PREFIX + "?[pls]ay$", 1, "id", 1), new RE(CONS + "y$", 1, "ied", 1),
			new RE("^(fling|cling|hang)$", 3, "ung", 0), new RE("(([sfc][twlp]?r?|w?r)ing)$", 3, "ang", 1),
			new RE("^" + VERBAL_PREFIX + "?(bend|spend|send|lend|spend)$", 1, "t", 0),
			new RE("^" + VERBAL_PREFIX + "?lie$", 2, "ay", 0),
			new RE("^" + VERBAL_PREFIX + "?(weep|sleep|sweep|creep|keep)$", 2, "pt", 0),
			new RE("^" + VERBAL_PREFIX + "?(sell|tell)$", 3, "old", 0),
			new RE("^" + VERBAL_PREFIX + "?do$", 1, "id", 0), new RE("^" + VERBAL_PREFIX + "?dig$", 2, "ug", 0),
			new RE("^behave$", 0, "d", 0), new RE("^(have)$", 2, "d", 0), new RE("(sink|drink)$", 3, "ank", 0),
			new RE("^swing$", 3, "ung", 0), new RE("^be$", 2, "was", 0), new RE("^outfight$", 4, "ought", 0),
			new RE("^tarmac", 0, "ked", 0), new RE("^abide$", 3, "ode", 0), new RE("^aby$", 1, "ought", 0),
			new RE("^become$", 3, "ame", 0), new RE("^begird$", 3, "irt", 0), new RE("^outlie$", 2, "ay", 0),
			new RE("^rebind$", 3, "ound", 0), new RE("^shit$", 3, "hat", 0), new RE("^bereave$", 4, "eft", 0),
			new RE("^foreswear$", 3, "ore", 0), new RE("^bename$", 3, "empt", 0), new RE("^beseech$", 4, "ought", 0),
			new RE("^bethink$", 3, "ought", 0), new RE("^bleed$", 4, "led", 0), new RE("^bog-down$", 5, "ged-down", 0),
			new RE("^buy$", 2, "ought", 0), new RE("^bind$", 3, "ound", 0), new RE("^(.*)feed$", 4, "fed", 0),
			new RE("^breed$", 4, "red", 0), new RE("^brei$", 2, "eid", 0), new RE("^bring$", 3, "ought", 0),
			new RE("^build$", 3, "ilt", 0), new RE("^come$", 3, "ame", 0), new RE("^catch$", 3, "ught", 0),
			new RE("^clothe$", 5, "lad", 0), new RE("^crossbreed$", 4, "red", 0), new RE("^deal$", 2, "alt", 0),
			new RE("^dow$", 1, "ught", 0), new RE("^dream$", 2, "amt", 0), new RE("^dwell$", 3, "elt", 0),
			new RE("^enwind$", 3, "ound", 0), new RE("^feel$", 3, "elt", 0), new RE("^flee$", 3, "led", 0),
			new RE("^floodlight$", 5, "lit", 0), new RE("^arise$", 3, "ose", 0), new RE("^eat$", 3, "ate", 0),
			new RE("^backbite$", 4, "bit", 0), new RE("^backslide$", 4, "lid", 0), new RE("^befall$", 3, "ell", 0),
			new RE("^begin$", 3, "gan", 0), new RE("^beget$", 3, "got", 0), new RE("^behold$", 3, "eld", 0),
			new RE("^bespeak$", 3, "oke", 0), new RE("^bestride$", 3, "ode", 0), new RE("^betake$", 3, "ook", 0),
			new RE("^bite$", 4, "bit", 0), new RE("^blow$", 3, "lew", 0), new RE("^bear$", 3, "ore", 0),
			new RE("^break$", 3, "oke", 0), new RE("^choose$", 4, "ose", 0), new RE("^cleave$", 4, "ove", 0),
			new RE("^countersink$", 3, "ank", 0), new RE("^drink$", 3, "ank", 0), new RE("^draw$", 3, "rew", 0),
			new RE("^drive$", 3, "ove", 0), new RE("^fall$", 3, "ell", 0), new RE("^fly$", 2, "lew", 0),
			new RE("^flyblow$", 3, "lew", 0), new RE("^forbid$", 2, "ade", 0), new RE("^forbear$", 3, "ore", 0),
			new RE("^foreknow$", 3, "new", 0), new RE("^foresee$", 3, "saw", 0), new RE("^forespeak$", 3, "oke", 0),
			new RE("^forego$", 2, "went", 0), new RE("^forgive$", 3, "ave", 0), new RE("^forget$", 3, "got", 0),
			new RE("^forsake$", 3, "ook", 0), new RE("^forspeak$", 3, "oke", 0), new RE("^forswear$", 3, "ore", 0),
			new RE("^forgo$", 2, "went", 0), new RE("^fight$", 4, "ought", 0), new RE("^find$", 3, "ound", 0),
			new RE("^freeze$", 4, "oze", 0), new RE("^give$", 3, "ave", 0), new RE("^geld$", 3, "elt", 0),
			new RE("^gen-up$", 3, "ned-up", 0), new RE("^ghostwrite$", 3, "ote", 0), new RE("^get$", 3, "got", 0),
			new RE("^grow$", 3, "rew", 0), new RE("^grind$", 3, "ound", 0), new RE("^hear$", 2, "ard", 0),
			new RE("^hold$", 3, "eld", 0), new RE("^hide$", 4, "hid", 0), new RE("^honey$", 2, "ied", 0),
			new RE("^inbreed$", 4, "red", 0), new RE("^indwell$", 3, "elt", 0), new RE("^interbreed$", 4, "red", 0),
			new RE("^interweave$", 4, "ove", 0), new RE("^inweave$", 4, "ove", 0), new RE("^ken$", 2, "ent", 0),
			new RE("^kneel$", 3, "elt", 0), new RE("^^know$$", 3, "new", 0), new RE("^leap$", 2, "apt", 0),
			new RE("^learn$", 2, "rnt", 0), new RE("^lead$", 4, "led", 0), new RE("^leave$", 4, "eft", 0),
			new RE("^light$", 5, "lit", 0), new RE("^lose$", 3, "ost", 0), new RE("^make$", 3, "ade", 0),
			new RE("^mean$", 2, "ant", 0), new RE("^meet$", 4, "met", 0), new RE("^misbecome$", 3, "ame", 0),
			new RE("^misdeal$", 2, "alt", 0), new RE("^misgive$", 3, "ave", 0), new RE("^mishear$", 2, "ard", 0),
			new RE("^mislead$", 4, "led", 0), new RE("^mistake$", 3, "ook", 0), new RE("^misunderstand$", 3, "ood", 0),
			new RE("^outbreed$", 4, "red", 0), new RE("^outgrow$", 3, "rew", 0), new RE("^outride$", 3, "ode", 0),
			new RE("^outshine$", 3, "one", 0), new RE("^outshoot$", 4, "hot", 0), new RE("^outstand$", 3, "ood", 0),
			new RE("^outthink$", 3, "ought", 0), new RE("^outgo$", 2, "went", 0), new RE("^outwear$", 3, "ore", 0),
			new RE("^overblow$", 3, "lew", 0), new RE("^overbear$", 3, "ore", 0), new RE("^overbuild$", 3, "ilt", 0),
			new RE("^overcome$", 3, "ame", 0), new RE("^overdraw$", 3, "rew", 0), new RE("^overdrive$", 3, "ove", 0),
			new RE("^overfly$", 2, "lew", 0), new RE("^overgrow$", 3, "rew", 0), new RE("^overhear$", 2, "ard", 0),
			new RE("^overpass$", 3, "ast", 0), new RE("^override$", 3, "ode", 0), new RE("^oversee$", 3, "saw", 0),
			new RE("^overshoot$", 4, "hot", 0), new RE("^overthrow$", 3, "rew", 0), new RE("^overtake$", 3, "ook", 0),
			new RE("^overwind$", 3, "ound", 0), new RE("^overwrite$", 3, "ote", 0), new RE("^partake$", 3, "ook", 0),
			new RE("^" + VERBAL_PREFIX + "?run$", 2, "an", 0), new RE("^ring$", 3, "ang", 0),
			new RE("^rebuild$", 3, "ilt", 0), new RE("^red", 0), new RE("^reave$", 4, "eft", 0),
			new RE("^remake$", 3, "ade", 0), new RE("^resit$", 3, "sat", 0), new RE("^rethink$", 3, "ought", 0),
			new RE("^retake$", 3, "ook", 0), new RE("^rewind$", 3, "ound", 0), new RE("^rewrite$", 3, "ote", 0),
			new RE("^ride$", 3, "ode", 0), new RE("^rise$", 3, "ose", 0), new RE("^reeve$", 4, "ove", 0),
			new RE("^sing$", 3, "ang", 0), new RE("^sink$", 3, "ank", 0), new RE("^sit$", 3, "sat", 0),
			new RE("^see$", 3, "saw", 0), new RE("^shoe$", 3, "hod", 0), new RE("^shine$", 3, "one", 0),
			new RE("^shake$", 3, "ook", 0), new RE("^shoot$", 4, "hot", 0), new RE("^shrink$", 3, "ank", 0),
			new RE("^shrive$", 3, "ove", 0), new RE("^sightsee$", 3, "saw", 0), new RE("^ski$", 1, "i'd", 0),
			new RE("^skydive$", 3, "ove", 0), new RE("^slay$", 3, "lew", 0), new RE("^slide$", 4, "lid", 0),
			new RE("^slink$", 3, "unk", 0), new RE("^smite$", 4, "mit", 0), new RE("^seek$", 3, "ought", 0),
			new RE("^spit$", 3, "pat", 0), new RE("^speed$", 4, "ped", 0), new RE("^spellbind$", 3, "ound", 0),
			new RE("^spoil$", 2, "ilt", 0), new RE("^speak$", 3, "oke", 0), new RE("^spotlight$", 5, "lit", 0),
			new RE("^spring$", 3, "ang", 0), new RE("^spin$", 3, "pun", 0), new RE("^stink$", 3, "ank", 0),
			new RE("^steal$", 3, "ole", 0), new RE("^stand$", 3, "ood", 0), new RE("^stave$", 3, "ove", 0),
			new RE("^stride$", 3, "ode", 0), new RE("^strive$", 3, "ove", 0), new RE("^strike$", 3, "uck", 0),
			new RE("^stick$", 3, "uck", 0), new RE("^swim$", 3, "wam", 0), new RE("^swear$", 3, "ore", 0),
			new RE("^teach$", 4, "aught", 0), new RE("^think$", 3, "ought", 0), new RE("^throw$", 3, "rew", 0),
			new RE("^take$", 3, "ook", 0), new RE("^tear$", 3, "ore", 0), new RE("^transship$", 4, "hip", 0),
			new RE("^tread$", 4, "rod", 0), new RE("^typewrite$", 3, "ote", 0), new RE("^unbind$", 3, "ound", 0),
			new RE("^unclothe$", 5, "lad", 0), new RE("^underbuy$", 2, "ought", 0), new RE("^undergird$", 3, "irt", 0),
			new RE("^undershoot$", 4, "hot", 0), new RE("^understand$", 3, "ood", 0),
			new RE("^undertake$", 3, "ook", 0), new RE("^undergo$", 2, "went", 0), new RE("^underwrite$", 3, "ote", 0),
			new RE("^unfreeze$", 4, "oze", 0), new RE("^unlearn$", 2, "rnt", 0), new RE("^unmake$", 3, "ade", 0),
			new RE("^unreeve$", 4, "ove", 0), new RE("^unspeak$", 3, "oke", 0), new RE("^unstick$", 3, "uck", 0),
			new RE("^unswear$", 3, "ore", 0), new RE("^unteach$", 4, "aught", 0), new RE("^unthink$", 3, "ought", 0),
			new RE("^untread$", 4, "rod", 0), new RE("^unwind$", 3, "ound", 0), new RE("^upbuild$", 3, "ilt", 0),
			new RE("^uphold$", 3, "eld", 0), new RE("^upheave$", 4, "ove", 0), new RE("^uprise$", 3, "ose", 0),
			new RE("^upspring$", 3, "ang", 0), new RE("^go$", 2, "went", 0), new RE("^wiredraw$", 3, "rew", 0),
			new RE("^withdraw$", 3, "rew", 0), new RE("^withhold$", 3, "eld", 0), new RE("^withstand$", 3, "ood", 0),
			new RE("^wake$", 3, "oke", 0), new RE("^win$", 3, "won", 0), new RE("^wear$", 3, "ore", 0),
			new RE("^wind$", 3, "ound", 0), new RE("^weave$", 4, "ove", 0), new RE("^write$", 3, "ote", 0),
			new RE("^trek$", 1, "cked", 0), new RE("^ko$", 1, "o'd", 0), new RE("^bid", 2, "ade", 0),
			new RE("^win$", 2, "on", 0), new RE("^swim", 2, "am", 0), new RE("e$", 0, "d", 1),

			// Null past forms
			new RE("^" + VERBAL_PREFIX + "?(cast|thrust|typeset|cut|bid|upset|wet|bet|cut|hit|hurt|inset|"
					+ "let|cost|burst|beat|beset|set|upset|offset|put|quit|wed|typeset|"
					+ "wed|spread|split|slit|read|run|shut|shed|lay)$", 0) };

	private static final RE[] PRESENT_RULES = { new RE("^aby$", 0, "es", 0), new RE("^bog-down$", 5, "s-down", 0),
			new RE("^chivy$", 1, "vies", 0), new RE("^gen-up$", 3, "s-up", 0), new RE("^prologue$", 3, "gs", 0),
			new RE("^picknic$", 0, "ks", 0), new RE("^ko$", 0, "'s", 0), new RE("[osz]$", 0, "es", 1),
			new RE("^have$", 2, "s", 0), new RE(CONS + "y$", 1, "ies", 1), new RE("^be$", 2, "is"),
			new RE("([zsx]|ch|sh)$", 0, "es", 1) };

	private static final String[] VERB_CONS_DOUBLING = { "abat", "abet", "abhor", "abut", "accur", "acquit", "adlib",
			"admit", "aerobat", "aerosol", "agendaset", "allot", "alot", "anagram", "annul", "appal", "apparel",
			"armbar", "aver", "babysit", "airdrop", "appal", "blackleg", "bobsled", "bur", "chum", "confab",
			"counterplot", "curet", "dib", "backdrop", "backfil", "backflip", "backlog", "backpedal", "backslap",
			"backstab", "bag", "balfun", "ballot", "ban", "bar", "barbel", "bareleg", "barrel", "bat", "bayonet",
			"becom", "bed", "bedevil", "bedwet", "beenhop", "befit", "befog", "beg", "beget", "begin", "bejewel",
			"bemedal", "benefit", "benum", "beset", "besot", "bestir", "bet", "betassel", "bevel", "bewig", "bib",
			"bid", "billet", "bin", "bip", "bit", "bitmap", "blab", "blag", "blam", "blan", "blat", "bles", "blim",
			"blip", "blob", "bloodlet", "blot", "blub", "blur", "bob", "bodypop", "bog", "booby-trap", "boobytrap",
			"booksel", "bootleg", "bop", "bot", "bowel", "bracket", "brag", "brig", "brim", "bud", "buffet", "bug",
			"bullshit", "bum", "bun", "bus", "but", "cab", "cabal", "cam", "can", "cancel", "cap", "caracol", "caravan",
			"carburet", "carnap", "carol", "carpetbag", "castanet", "cat", "catcal", "catnap", "cavil", "chan",
			"chanel", "channel", "chap", "char", "chargecap", "chat", "chin", "chip", "chir", "chirrup", "chisel",
			"chop", "chug", "chur", "clam", "clap", "clearcut", "clip", "clodhop", "clog", "clop", "closet", "clot",
			"club", "co-occur", "co-program", "co-refer", "co-run", "co-star", "cob", "cobweb", "cod", "coif", "com",
			"combat", "comit", "commit", "compel", "con", "concur", "confer", "confiscat", "control", "cop", "coquet",
			"coral", "corbel", "corral", "cosset", "cotransmit", "councel", "council", "counsel", "court-martial",
			"crab", "cram", "crap", "crib", "crop", "crossleg", "cub", "cudgel", "cum", "cun", "cup", "cut", "dab",
			"dag", "dam", "dan", "dap", "daysit", "de-control", "de-gazet", "de-hul", "de-instal", "de-mob",
			"de-program", "de-rig", "de-skil", "deadpan", "debag", "debar", "log", "decommit", "decontrol", "defer",
			"defog", "deg", "degas", "deinstal", "demit", "demob", "demur", "den", "denet", "depig", "depip", "depit",
			"der", "deskil", "deter", "devil", "diagram", "dial", "dig", "dim", "din", "dip", "disbar", "disbud",
			"discomfit", "disembed", "disembowel", "dishevel", "disinter", "dispel", "disprefer", "distil", "dog",
			"dognap", "don", "doorstep", "dot", "dowel", "drag", "drat", "driftnet", "distil", "egotrip", "enrol",
			"enthral", "extol", "fulfil", "gaffe", "golliwog", "idyl", "inspan", "drip", "drivel", "drop", "drub",
			"drug", "drum", "dub", "duel", "dun", "dybbuk", "earwig", "eavesdrop", "ecolabel", "eitherspigot",
			"electroblot", "embed", "emit", "empanel", "enamel", "endlabel", "endtrim", "enrol", "enthral", "entrammel",
			"entrap", "enwrap", "equal", "equip", "estop", "exaggerat", "excel", "expel", "extol", "fag", "fan",
			"farewel", "fat", "featherbed", "feget", "fet", "fib", "fig", "fin", "fingerspel", "fingertip", "fit",
			"flab", "flag", "flap", "flip", "flit", "flog", "flop", "fob", "focus", "fog", "footbal", "footslog", "fop",
			"forbid", "forget", "format", "fortunetel", "fot", "foxtrot", "frag", "freefal", "fret", "frig", "frip",
			"frog", "frug", "fuel", "fufil", "fulfil", "fullyfit", "fun", "funnel", "fur", "furpul", "gab", "gad",
			"gag", "gam", "gambol", "gap", "garot", "garrot", "gas", "gat", "gel", "gen", "get", "giftwrap", "gig",
			"gimbal", "gin", "glam", "glenden", "glendin", "globetrot", "glug", "glut", "gob", "goldpan", "goostep",
			"gossip", "grab", "gravel", "grid", "grin", "grip", "grit", "groundhop", "grovel", "grub", "gum", "gun",
			"gunrun", "gut", "gyp", "haircut", "ham", "han", "handbag", "handicap", "handknit", "handset", "hap",
			"hareleg", "hat", "headbut", "hedgehop", "hem", "hen", "hiccup", "highwal", "hip", "hit", "hobnob", "hog",
			"hop", "horsewhip", "hostel", "hot", "hotdog", "hovel", "hug", "hum", "humbug", "hup", "hushkit", "hut",
			"illfit", "imbed", "immunblot", "immunoblot", "impannel", "impel", "imperil", "incur", "infer", "infil",
			"inflam", "initial", "input", "inset", "instil", "inter", "interbed", "intercrop", "intercut", "interfer",
			"instal", "instil", "intermit", "japan", "jug", "kris", "manumit", "mishit", "mousse", "mud", "interwar",
			"jab", "jag", "jam", "jar", "jawdrop", "jet", "jetlag", "jewel", "jib", "jig", "jitterbug", "job", "jog",
			"jog-trot", "jot", "jut", "ken", "kennel", "kid", "kidnap", "kip", "kissogram", "kit", "knap", "kneecap",
			"knit", "knob", "knot", "kor", "label", "lag", "lam", "lap", "lavel", "leafcut", "leapfrog", "leg", "lem",
			"lep", "let", "level", "libel", "lid", "lig", "lip", "lob", "log", "lok", "lollop", "longleg", "lop",
			"lowbal", "lug", "mackerel", "mahom", "man", "map", "mar", "marshal", "marvel", "mat", "matchwin", "metal",
			"micro-program", "microplan", "microprogram", "milksop", "mis-cal", "mis-club", "mis-spel", "miscal",
			"mishit", "mislabel", "mit", "mob", "mod", "model", "mohmam", "monogram", "mop", "mothbal", "mug",
			"multilevel", "mum", "nab", "nag", "nan", "nap", "net", "nightclub", "nightsit", "nip", "nod", "nonplus",
			"norkop", "nostril", "not", "nut", "nutmeg", "occur", "ocur", "offput", "offset", "omit", "ommit", "onlap",
			"out-general", "out-gun", "out-jab", "out-plan", "out-pol", "out-pul", "out-put", "out-run", "out-sel",
			"outbid", "outcrop", "outfit", "outgas", "outgun", "outhit", "outjab", "outpol", "output", "outrun",
			"outship", "outshop", "outsin", "outstrip", "outswel", "outspan", "overcrop", "pettifog", "photostat",
			"pouf", "preset", "prim", "pug", "ret", "rosin", "outwit", "over-commit", "over-control", "over-fil",
			"over-fit", "over-lap", "over-model", "over-pedal", "over-pet", "over-run", "over-sel", "over-step",
			"over-tip", "over-top", "overbid", "overcal", "overcommit", "overcontrol", "overcrap", "overdub", "overfil",
			"overhat", "overhit", "overlap", "overman", "overplot", "overrun", "overshop", "overstep", "overtip",
			"overtop", "overwet", "overwil", "pad", "paintbal", "pan", "panel", "paperclip", "par", "parallel",
			"parcel", "partiescal", "pat", "patrol", "pedal", "peewit", "peg", "pen", "pencil", "pep", "permit", "pet",
			"petal", "photoset", "phototypeset", "phut", "picket", "pig", "pilot", "pin", "pinbal", "pip", "pipefit",
			"pipet", "pit", "plan", "plit", "plod", "plop", "plot", "plug", "plumet", "plummet", "pod", "policyset",
			"polyfil", "ponytrek", "pop", "pot", "pram", "prebag", "predistil", "predril", "prefer", "prefil",
			"preinstal", "prep", "preplan", "preprogram", "prizewin", "prod", "profer", "prog", "program", "prop",
			"propel", "pub", "pummel", "pun", "pup", "pushfit", "put", "quarel", "quarrel", "quickskim", "quickstep",
			"quickwit", "quip", "quit", "quivertip", "quiz", "rabbit", "rabit", "radiolabel", "rag", "ram", "ramrod",
			"rap", "rat", "ratecap", "ravel", "re-admit", "re-cal", "re-cap", "re-channel", "re-dig", "re-dril",
			"re-emit", "re-fil", "re-fit", "re-flag", "re-format", "re-fret", "re-hab", "re-instal", "re-inter",
			"re-lap", "re-let", "re-map", "re-metal", "re-model", "re-pastel", "re-plan", "re-plot", "re-plug",
			"re-pot", "re-program", "re-refer", "re-rig", "re-rol", "re-run", "re-sel", "re-set", "re-skin", "re-stal",
			"re-submit", "re-tel", "re-top", "re-transmit", "re-trim", "re-wrap", "readmit", "reallot", "rebel",
			"rebid", "rebin", "rebut", "recap", "rechannel", "recommit", "recrop", "recur", "recut", "red", "redril",
			"refer", "refit", "reformat", "refret", "refuel", "reget", "regret", "reinter", "rejig", "rekit", "reknot",
			"relabel", "relet", "rem", "remap", "remetal", "remit", "remodel", "reoccur", "rep", "repel", "repin",
			"replan", "replot", "repol", "repot", "reprogram", "rerun", "reset", "resignal", "resit", "reskil",
			"resubmit", "retransfer", "retransmit", "retro-fit", "retrofit", "rev", "revel", "revet", "rewrap", "rib",
			"richochet", "ricochet", "rid", "rig", "rim", "ringlet", "rip", "rit", "rival", "rivet", "roadrun", "rob",
			"rocket", "rod", "roset", "rot", "rowel", "rub", "run", "runnel", "rut", "sab", "sad", "sag", "sandbag",
			"sap", "scab", "scalpel", "scam", "scan", "scar", "scat", "schlep", "scrag", "scram", "shall", "sled",
			"smut", "stet", "sulfuret", "trepan", "unrip", "unstop", "whir", "whop", "wig", "scrap", "scrat", "scrub",
			"scrum", "scud", "scum", "scur", "semi-control", "semi-skil", "semi-skim", "semiskil", "sentinel", "set",
			"shag", "sham", "shed", "shim", "shin", "ship", "shir", "shit", "shlap", "shop", "shopfit", "shortfal",
			"shot", "shovel", "shred", "shrinkwrap", "shrivel", "shrug", "shun", "shut", "side-step", "sideslip",
			"sidestep", "signal", "sin", "sinbin", "sip", "sit", "skid", "skim", "skin", "skip", "skir", "skrag",
			"slab", "slag", "slam", "slap", "slim", "slip", "slit", "slob", "slog", "slop", "slot", "slowclap", "slug",
			"slum", "slur", "smit", "snag", "snap", "snip", "snivel", "snog", "snorkel", "snowcem", "snub", "snug",
			"sob", "sod", "softpedal", "son", "sop", "spam", "span", "spar", "spat", "spiderweb", "spin", "spiral",
			"spit", "splat", "split", "spot", "sprag", "spraygun", "sprig", "springtip", "spud", "spur", "squat",
			"squirrel", "stab", "stag", "star", "stem", "sten", "stencil", "step", "stir", "stop", "storytel", "strap",
			"strim", "strip", "strop", "strug", "strum", "strut", "stub", "stud", "stun", "sub", "subcrop", "sublet",
			"submit", "subset", "suedetrim", "sum", "summit", "sun", "suntan", "sup", "super-chil", "superad", "swab",
			"swag", "swan", "swap", "swat", "swig", "swim", "swivel", "swot", "tab", "tag", "tan", "tansfer", "tap",
			"tar", "tassel", "tat", "tefer", "teleshop", "tendril", "terschel", "th'strip", "thermal", "thermostat",
			"thin", "throb", "thrum", "thud", "thug", "tightlip", "tin", "tinsel", "tip", "tittup", "toecap", "tog",
			"tom", "tomorrow", "top", "tot", "total", "towel", "traget", "trainspot", "tram", "trammel", "transfer",
			"tranship", "transit", "transmit", "transship", "trap", "travel", "trek", "trendset", "trim", "trip",
			"tripod", "trod", "trog", "trot", "trousseaushop", "trowel", "trup", "tub", "tug", "tunnel", "tup", "tut",
			"twat", "twig", "twin", "twit", "typeset", "tyset", "un-man", "unban", "unbar", "unbob", "uncap", "unclip",
			"uncompel", "undam", "under-bil", "under-cut", "under-fit", "under-pin", "under-skil", "underbid",
			"undercut", "underlet", "underman", "underpin", "unfit", "unfulfil", "unknot", "unlip", "unlywil", "unman",
			"unpad", "unpeg", "unpin", "unplug", "unravel", "unrol", "unscrol", "unsnap", "unstal", "unstep", "unstir",
			"untap", "unwrap", "unzip", "up", "upset", "upskil", "upwel", "ven", "verbal", "vet", "victual", "vignet",
			"wad", "wag", "wainscot", "wan", "war", "water-log", "waterfal", "waterfil", "waterlog", "weasel", "web",
			"wed", "wet", "wham", "whet", "whip", "whir", "whiteskin", "whiz", "whup", "wildcat", "win", "windmil",
			"wit", "woodchop", "woodcut", "wor", "worship", "wrap", "wiretap", "yen", "yak", "yap", "yarnspin", "yip",
			"yodel", "zag", "zap", "zig", "zig-zag", "zigzag", "zip", "ztrip", "hand-bag", "hocus", "hocus-pocus" };

	private static final Map<String, Object> PAST_PART_RULESET;
	static {
		PAST_PART_RULESET = new HashMap<>();
		PAST_PART_RULESET.put("name", "PAST_PARTICIPLE");
		PAST_PART_RULESET.put("defaultRule", new RE(ANY_STEM, 0, "ed", 2));
		PAST_PART_RULESET.put("rules", PAST_PART_RULES);
		PAST_PART_RULESET.put("doubling", true);
	}

	private static final Map<String, Object> PRESENT_PART_RULESET;
	static {
		PRESENT_PART_RULESET = new HashMap<>();
		PRESENT_PART_RULESET.put("name", "ING_FORM");
		PRESENT_PART_RULESET.put("defaultRule", new RE(ANY_STEM, 0, "ing", 2));
		PRESENT_PART_RULESET.put("rules", ING_FORM_RULES);
		PRESENT_PART_RULESET.put("doubling", true);
	}

	private static final Map<String, Object> PAST_RULESET;
	static {
		PAST_RULESET = new HashMap<>();
		PAST_RULESET.put("name", "PAST_TENSE");
		PAST_RULESET.put("defaultRule", new RE(ANY_STEM, 0, "ed", 2));
		PAST_RULESET.put("rules", PAST_RULES);
		PAST_RULESET.put("doubling", true);
	}

	private static final Map<String, Object> PRESENT_RULESET;
	static {
		PRESENT_RULESET = new HashMap<>();
		PRESENT_RULESET.put("name", "PRESENT_TENSE");
		PRESENT_RULESET.put("defaultRule", new RE(ANY_STEM, 0, "s", 2));
		PRESENT_RULESET.put("rules", PRESENT_RULES);
		PRESENT_RULESET.put("doubling", false);
	}

	private static final String[] TO_BE = new String[] { "am", "are", "is", "was", "were" };
	private static final Pattern PastPartRE1 = Pattern.compile("^(writt|ridd|chidd|swoll)$");
	private static final Pattern PastPartRE2 = Pattern.compile("[ndt]$");

	public static String conjugate(String verb, String args) {

		if (RE.test("^[123][SP](Pr|Pa|Fu)$", args)) {
			Map<String, Object> opts = RiTa.opts();
			opts.put("person", Integer.parseInt(args.substring(0, 1)));
			opts.put("number", args.charAt(1) == 'S' ? RiTa.SINGULAR : RiTa.PLURAL);
			String tense = args.substring(2);
			if (tense.equals("Pr")) opts.put("tense", RiTa.PRESENT);
			if (tense.equals("Fu")) opts.put("tense", RiTa.FUTURE);
			if (tense.equals("Pa")) opts.put("tense", RiTa.PAST);
			return conjugate(verb, opts);
		}

		return conjugate(verb, Util.stringArgs(args));
	}

	public static String conjugate(String verb, Map<String, Object> opts) {

		if (verb == null) throw new RiTaException("conjugate requires a verb");

		if (opts == null || opts.size() == 0 || verb.length() == 0) {
			return verb;
		}

		int number = Util.intOpt("number", opts, RiTa.SINGULAR);
		int person = Util.intOpt("person", opts, RiTa.FIRST);
		int tense = Util.intOpt("tense", opts, RiTa.PRESENT);
		int form = Util.intOpt("form", opts, RiTa.NORMAL);

		boolean perfect = Util.boolOpt("perfect", opts);
		boolean passive = Util.boolOpt("passive", opts);
		boolean progressive = Util.boolOpt("progressive", opts);
		boolean interrogative = Util.boolOpt("interrogative", opts);

		// ----------------------- start --------------------------

		String v = verb.toLowerCase();

		// make sure verb is base form
		if (!Arrays.asList(RiTa.tagger.allTags(v)).contains("vb")) {
			v = unconjugate(v) != null && unconjugate(v).length() > 0 ? unconjugate(v) : v;
		}

		List<String> list = Arrays.asList(TO_BE);
		if (list.contains(v)) {
			v = "be"; // handle to-be forms
		}
		else {
			v = handleStem(v); // handle stems
		}

		String verbForm, frontVG = v, actualModal = null;
		ArrayList<String> conjs = new ArrayList<String>();

		if (form == RiTa.INFINITIVE) {
			actualModal = "to";
		}

		if (tense == RiTa.FUTURE) {
			actualModal = "will";
		}

		if (passive) {
			conjs.add(pastPart(frontVG));
			frontVG = "be";
		}

		if (progressive) {
			conjs.add(presentPart(frontVG));
			frontVG = "be";
		}

		if (perfect) {
			conjs.add(pastPart(frontVG));
			frontVG = "have";
		}

		if (actualModal != null) {
			conjs.add(frontVG);
			frontVG = null;
		}

		// Now inflect frontVG (if it exists) and push it on restVG
		if (frontVG != null) {
			if (form == RiTa.GERUND) { // gerund - use ING form

				String pp = presentPart(frontVG);

				// !@# not yet implemented! ??? WHAT?
				conjs.add(pp);
			}
			else if (interrogative && !frontVG.equals("be") && conjs.size() < 1) {

				conjs.add(frontVG);
			}
			else {

				verbForm = verbForm(frontVG, tense, person, number);
				conjs.add(verbForm);
			}
		}

		// add modal, and we're done
		if (actualModal != null)
			conjs.add(actualModal);

		String s = conjs.stream().reduce("", (acc, cur) -> cur + " " + acc);

		if (s.endsWith("peted"))
			throw new RiTaException("Unexpected output: " + s);

		return s.trim();
	}

	public static String unconjugate(String word) {
		//lazy load
		if (verbsEndingInE.length < 1) {
			verbsEndingInE = RiTa.search("e$", opts("limit", 999999, "minLength", 1, "pos", "vb"));
		}

		if (verbsEndingInDoubles.length < 1) {
			verbsEndingInDoubles = RiTa.search("([a-z])\\1+$", opts("limit", 999999, "minLength", 1, "pos", "vb"));
		}

		List<String> irregsInLex = Arrays.asList(IRREG_VERBS_LEX);
		if (irregsInLex.contains(word) && irregsInLex.indexOf(word) % 2 == 0) {
			return IRREG_VERBS_LEX[irregsInLex.indexOf(word) + 1];
		}
		else if (irregsInLex.contains(word) && irregsInLex.indexOf(word) % 2 == 1) {
			return word;
		}
		List<String> irregsNoLex = Arrays.asList(IRREG_VERBS_NOLEX);
		if (irregsNoLex.indexOf(word) > -1 && irregsNoLex.indexOf(word) % 2 == 0) {
			return IRREG_VERBS_NOLEX[irregsNoLex.indexOf(word)
					+ 1];
		}
		else if (irregsNoLex.indexOf(word) > -1 && irregsNoLex.indexOf(word) % 2 == 1) {
			return word;
		}

		// Verb lemmatization rules

		// 1) 3rd person present
		if (word.endsWith("s")) {

			if (word.endsWith("ies")) {
				return word.substring(0, word.length() - 3) + "y";
			}
			else if (Pattern.compile("[a-z]+(ch|s|sh|x|z|o)es$").matcher(word).matches()) {
				return word.substring(0, word.length() - 2);
			}
			return word.substring(0, word.length() - 1);
		}

		// 2) past forms
		else if (word.endsWith("ed")) {

			if (word.endsWith("ied")) {
				return word.substring(0, word.length() - 3) + "y";
			}
			else if (Pattern.compile("[a-z]+([a-z])\\1ed$").matcher(word).matches()) {
				if (Arrays.asList(verbsEndingInDoubles).contains(word.replaceAll("ed$", ""))) {
					return word.substring(0, word.length() - 2);
				}
				return word.substring(0, word.length() - 3);
			}
			else if (word.endsWith("ed")) {
				if (Arrays.asList(verbsEndingInE).contains(word.replaceAll("d$", ""))) {
					return word.substring(0, word.length() - 1);
				}
				else {
					return word.substring(0, word.length() - 2);
				}
			}
		}

		// 3) ends with 'ing'
		else if (word.endsWith("ing")) {
			if (Pattern.compile("[a-z]+([a-z])\\1ing$").matcher(word).matches()) {
				if (Arrays.asList(verbsEndingInDoubles).contains(word.replaceAll("ing$", ""))) {
					return word.substring(0, word.length() - 3);
				}
				return word.substring(0, word.length() - 4);
			}
			if (word.endsWith("ying")) {
				if (Arrays.asList(verbsEndingInE).contains(word.replaceAll("ying$", "ie"))) {
					return word.substring(0, word.length() - 4) + "ie";
				}
			}
			if (Arrays.asList(verbsEndingInE).contains(word.replaceAll("ing$", "e"))) {
				return word.substring(0, word.length() - 3) + "e";
			}

			return word.substring(0, word.length() - 3);

		}

		String[] tags = RiTa.tagger.allTags(word, RiTa.opts("noGuessing", true));
		boolean notAVerb = tags.length > 0;
		for (int i = 0; i < tags.length; i++) {
			if (tags[i].equals("vb")) {
				return word;
			}
			if (tags[i].contains("vb")) {
				notAVerb = false;
				break;
			}
		}
		if (notAVerb) return word;
		return word;
	}

	private static String checkRules(Map<String, Object> ruleset, String theVerb) {

		if (theVerb == null || theVerb.length() == 0) return "";

		theVerb = theVerb.trim();

		boolean dbug = false;
		String res, name = (String) ruleset.get("name");
		RE[] rules = (RE[]) ruleset.get("rules");
		RE defRule = (RE) ruleset.get("defaultRule");

		if (rules == null) System.err.println("no rule: " + (String) ruleset.get("name") + " of " + theVerb);

		if (Arrays.asList(MODALS).contains(theVerb)) return theVerb;

		for (int i = 0; i < rules.length; i++) {
			if (dbug) console.log("checkRules(" + name + ").fire(" + i + ")=" + rules[i]);
			if (rules[i].applies(theVerb)) {
				String got = rules[i].fire(theVerb);
				if (dbug) console.log("HIT(" + name + ").fire(" + i + ")=" + rules[i] + "_returns: " + got);
				return got;
			}
		}

		if (dbug) console.log("NO HIT!");

		if ((boolean) ruleset.get("doubling") && Arrays.asList(VERB_CONS_DOUBLING).contains(theVerb)) {
			if (dbug) console.log("doDoubling!");
			theVerb = doubleFinalConsonant(theVerb);
		}

		res = defRule.fire(theVerb);
		if (dbug) console.log("checkRules(" + name + ").returns: " + res);

		return res;
	}

	private static String doubleFinalConsonant(String word) {
		return word + word.charAt(word.length() - 1);
	}

	public static String pastTense(String theVerb, int pers, int numb) {
		if (theVerb.toLowerCase().equals("be")) {

			switch (numb) {

			case RiTa.SINGULAR:
				switch (pers) {

				case RiTa.FIRST:
					break;

				case RiTa.THIRD:
					return "was";

				case RiTa.SECOND:
					return "were";

				}
				break;

			case RiTa.PLURAL:
				return "were";
			}
		}

		return checkRules(PAST_RULESET, theVerb);
	}

	public static String presentTense(String theVerb) {
		return presentTense(theVerb, RiTa.FIRST);
	}

	public static String presentTense(String theVerb, int person) {
		return presentTense(theVerb, person, RiTa.SINGULAR);
	}

	public static String presentTense(String theVerb, int person, int number) {

		if ((person == RiTa.THIRD) && (number == RiTa.SINGULAR)) {
			return checkRules(PRESENT_RULESET, theVerb);
		}
		else if (theVerb.equals("be")) {

			if (number == RiTa.SINGULAR) {
				switch (person) {
				case RiTa.FIRST:
					return "am";
				case RiTa.SECOND:
					return "are";
				case RiTa.THIRD:
					return "is";
				}
			}
			else {
				return "are";
			}
		}
		return theVerb;
	}

	public static String presentPart(String verb) {
		return verb.equals("be") ? "being" : checkRules(PRESENT_PART_RULESET, verb);
	}

	public static String pastPart(String verb) {
		if (isPastParticiple(verb)) return verb;
		return checkRules(PAST_PART_RULESET, verb);
	}

	private static boolean isPastParticiple(String word) {

		String w = word.toLowerCase();
		Lexicon lex = RiTa.lexicon();
		String[] posArr = lex.posArr(w);

		// word in dict
		if (posArr != null && Arrays.asList(posArr).contains("vbn")) return true;

		// irregular
		if (Arrays.asList(IRREG_PAST_PART).contains(w)) return true;

		// ends with ed?
		if (w.endsWith("ed")) {
			String[] pos = lex.posArr(w.substring(0, w.length() - 1)); // created
			if (pos == null || pos.length == 0) pos = lex.posArr(w.substring(0, w.length() - 2)); // played
			if ((pos == null || pos.length == 0) && w.charAt(w.length() - 3) == w.charAt(w.length() - 4)) {
				pos = lex.posArr(w.substring(0, w.length() - 3)); // hopped
			}
			if ((pos == null || pos.length == 0) && w.endsWith("ied")) {
				pos = lex.posArr(w.substring(0, w.length() - 3) + "y"); // cried
			}
			if (pos != null && Arrays.asList(pos).contains("vb")) return true;
		}

		// ends with en?
		if (w.endsWith("en")) {
			String[] pos = lex.posArr(w.substring(0, w.length() - 1)); // driven
			if (pos == null || pos.length == 0 && w.length() > 1) {
					pos = lex.posArr(w.substring(0, w.length() - 2)); // eaten
					
					// fix to rita #150
					if (w.length() > 3 && w.charAt(w.length() - 3) == w.charAt(w.length() - 4)) {
						pos = lex.posArr(w.substring(0, w.length() - 3)); // forgotten
					}
			}

			List<String> posl = Arrays.asList(pos);
			if (pos != null && (posl.contains("vb") || posl.contains("vbd"))) return true;

			//special cases
			String stem = w.substring(0, w.length() - 2);
			if (PastPartRE1.matcher(stem).matches()) return true;
		}

		// ends with n,t,d
		if (PastPartRE2.matcher(w).matches()) {
			String[] pos = lex.posArr(w.substring(0, w.length() - 1));
			if (pos != null && Arrays.asList(pos).contains("vb")) return true;
		}

		return false;
	}

	private static String verbForm(String theVerb, int tense, int person, int number) {

		switch (tense) {
		case RiTa.PRESENT:
			return presentTense(theVerb, person, number);
		case RiTa.PAST:
			return pastTense(theVerb, person, number);
		}
		return theVerb;
	}

	private static String handleStem(String word) {
		if (RiTa.hasWord(word) && RiTa.isVerb(word))
			return word;
		Map<String, Object> searchArgs = new HashMap<String, Object>();
		searchArgs.put("pos", "vb");
		String w = word;
		while (w.length() > 1) {
			Pattern regex = Pattern.compile("^" + w);
			String[] guess = RiTa.search(regex, searchArgs);
			if (guess == null || guess.length == 0) {
				w = w.substring(0, w.length() - 1);
				continue;
			}
			// look for shorter words first
			Arrays.sort(guess, (a, b) -> Integer.compare(a.length(), b.length()));
			for (int i = 0; i < guess.length; i++) {
				if (word.equals(guess[i]))
					return word;
				if (RiTa.stem(guess[i]).equals(word))
					return guess[i];
				if (unconjugate(RiTa.stem(guess[i])).equals(word))
					return guess[i];
			}
			w = w.substring(0, w.length() - 1);
		}
		// can't find possible word in dict, return the input
		return word;
	}

}
