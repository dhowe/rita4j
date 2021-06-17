package rita;

import java.util.*;
import java.util.regex.Pattern;

import com.google.gson.Gson;

public class Conjugator {

	private static final String CONS = "[bcdfghjklmnpqrstvwxyz]";
	private static final String ANY_STEM = "^((\\w+)(-\\w+)*)(\\s((\\w+)(-\\w+)*))*$";
	private static final String VERBAL_PREFIX = "((be|with|pre|un|over|re|mis|under|out|up|fore|for|counter|co|sub)(-?))";
	private static final String[] MODALS = { "shall", "would", "may", "might", "ought", "should" };
	private static final String[] IRREGULAR_PAST_PART = { "done", "gone", "abode", "been", "begotten", "begun", "bent", "bid",
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

	private static final RE[] PAST_PARTICIPLE_RULES = {

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

	private static final RE[] PAST_TENSE_RULES = { new RE("^(reduce)$", 0, "d", 0),
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

	private static final RE[] PRESENT_TENSE_RULES = { new RE("^aby$", 0, "es", 0), new RE("^bog-down$", 5, "s-down", 0),
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

	private static final Map<String, Object> PAST_PARTICIPLE_RULESET;
	static {
		PAST_PARTICIPLE_RULESET = new HashMap<>();
		PAST_PARTICIPLE_RULESET.put("name", "PAST_PARTICIPLE");
		PAST_PARTICIPLE_RULESET.put("defaultRule", new RE(ANY_STEM, 0, "ed", 2));
		PAST_PARTICIPLE_RULESET.put("rules", PAST_PARTICIPLE_RULES);
		PAST_PARTICIPLE_RULESET.put("doubling", true);
	}

	private static final Map<String, Object> PRESENT_PARTICIPLE_RULESET;
	static {
		PRESENT_PARTICIPLE_RULESET = new HashMap<>();
		PRESENT_PARTICIPLE_RULESET.put("name", "ING_FORM");
		PRESENT_PARTICIPLE_RULESET.put("defaultRule", new RE(ANY_STEM, 0, "ing", 2));
		PRESENT_PARTICIPLE_RULESET.put("rules", ING_FORM_RULES);
		PRESENT_PARTICIPLE_RULESET.put("doubling", true);
	}

	private static final Map<String, Object> PAST_TENSE_RULESET;
	static {
		PAST_TENSE_RULESET = new HashMap<>();
		PAST_TENSE_RULESET.put("name", "PAST_TENSE");
		PAST_TENSE_RULESET.put("defaultRule", new RE(ANY_STEM, 0, "ed", 2));
		PAST_TENSE_RULESET.put("rules", PAST_TENSE_RULES);
		PAST_TENSE_RULESET.put("doubling", true);
	}

	private static final Map<String, Object> PRESENT_TENSE_RULESET;
	static {
		PRESENT_TENSE_RULESET = new HashMap<>();
		PRESENT_TENSE_RULESET.put("name", "PRESENT_TENSE");
		PRESENT_TENSE_RULESET.put("defaultRule", new RE(ANY_STEM, 0, "s", 2));
		PRESENT_TENSE_RULESET.put("rules", PRESENT_TENSE_RULES);
		PRESENT_TENSE_RULESET.put("doubling", false);
	}

	private static final String[] TO_BE = new String[] { "am", "are", "is", "was", "were" };

	public static String conjugate(String verb, String args) {
		
		if (RE.test("^[123][SP](Pr|Pa|Fu)$", args)) {
      Map<String, Object> opts = RiTa.opts();
      opts.put("person", Integer.parseInt(args.substring(0,1)));
      opts.put("number", args.charAt(1) == 'S' ? RiTa.SINGULAR : RiTa.PLURAL);
      String tense = args.substring(2);
      if (tense.equals("Pr")) opts.put("tense", RiTa.PRESENT);
      if (tense.equals("Fu"))  opts.put("tense", RiTa.FUTURE);
      if (tense.equals("Pa"))  opts.put("tense", RiTa.PAST);
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

		List<String> list = Arrays.asList(TO_BE);
		if (list.contains(v)) {
			v = "be"; // handle to-be forms
		} else {
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

	private static String checkRules(Map<String, Object> ruleset, String theVerb) {

		if (theVerb == null || theVerb.length() == 0)
			return "";
		theVerb = theVerb.trim();

		boolean dbug = false;
		String res;
		String name = (String) ruleset.get("name");
		RE[] rules = (RE[]) ruleset.get("rules");
		RE defRule = (RE) ruleset.get("defaultRule");

		if (rules == null)
			System.err.println("no rule: " + (String) ruleset.get("name") + " of " + theVerb);
		if (Arrays.asList(MODALS).contains(theVerb))
			return theVerb;

		for (int i = 0; i < rules.length; i++) {
			if (dbug)
				console.log("checkRules(" + name + ").fire(" + i + ")=" + rules[i]);
			if (rules[i].applies(theVerb)) {
				String got = rules[i].fire(theVerb);
				if (dbug)
					console.log("HIT(" + name + ").fire(" + i + ")=" + rules[i] + "_returns: " + got);
				return got;
			}
		}
		if (dbug)
			console.log("NO HIT!");

		if ((boolean) ruleset.get("doubling") && Arrays.asList(VERB_CONS_DOUBLING).contains(theVerb)) {
			if (dbug)
				console.log("doDoubling!");
			theVerb = doubleFinalConsonant(theVerb);
		}
		res = defRule.fire(theVerb);
		if (dbug)
			console.log("checkRules(" + name + ").returns: " + res);
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

		return checkRules(PAST_TENSE_RULESET, theVerb);
	}
	
	public static String presentTense(String theVerb) {
		return presentTense(theVerb, RiTa.FIRST);
	}	
	
	public static String presentTense(String theVerb, int person) {
		return presentTense(theVerb, person, RiTa.SINGULAR);
	}	
  
	public static String presentTense(String theVerb, int person, int number) {

		if ((person == RiTa.THIRD) && (number == RiTa.SINGULAR)) {
			return checkRules(PRESENT_TENSE_RULESET, theVerb);
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
		return verb.equals("be") ? "being" : checkRules(PRESENT_PARTICIPLE_RULESET, verb);
	}

	public static String pastPart(String verb) {
		if (isPastParticiple(verb)) return verb;
		return checkRules(PAST_PARTICIPLE_RULESET, verb);
	}

	private static boolean isPastParticiple(String word) {
		String w = word.toLowerCase();
		// word in dict
		if (RiTa.lexicon().posArr(w) != null && Arrays.asList(RiTa.lexicon().posArr(word)).contains("vbn")) return true;
		//irregular
		if (Arrays.asList(IRREGULAR_PAST_PART).contains(w)) return true;
		// ends with ed?
		if (w.endsWith("ed")) {
			String[] pos = RiTa.lexicon().posArr(w.substring(0, w.length() - 1)); // created
			if (pos == null || pos.length == 0) pos = RiTa.lexicon().posArr(w.substring(0, w.length() - 2)); // played
			if ((pos == null || pos.length == 0) && w.charAt(w.length() - 3) == w.charAt(w.length() - 4)) {
				pos = RiTa.lexicon().posArr(w.substring(0, w.length() - 3)); // hopped
			}
			if ((pos == null || pos.length == 0) && w.endsWith("ied")) {
				pos = RiTa.lexicon().posArr(w.substring(0, w.length() - 3) + "y"); // cried
			}
			if (pos != null && Arrays.asList(pos).contains("vb")) return true;
		}
		// ends with en?
		if (w.endsWith("en")) {
			String[] pos = RiTa.lexicon().posArr(w.substring(0, w.length() - 1)); // driven
			if (pos == null || pos.length == 0) pos = RiTa.lexicon().posArr(w.substring(0, w.length() - 2)); // eaten
			if ((pos == null || pos.length == 0)&& w.charAt(w.length() - 3) == w.charAt(w.length() - 4)) {
				pos = RiTa.lexicon().posArr(w.substring(0, w.length() - 3)); // forgotten
			}
			if (pos != null && (Arrays.asList(pos).contains("vb") || Arrays.asList(pos).contains("vbd"))) return true;
			//special cases
			String stem = w.substring(0, w.length() - 2);
			if (Pattern.compile("^(writt|ridd|chidd|swoll)$").matcher(stem).matches()) return true;
		}
		// ends with n,t,d
		if (Pattern.compile("[ndt]$").matcher(w).matches()) {
			String[] pos = RiTa.lexicon().posArr(w.substring(0, w.length() - 1));
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
		searchArgs.put("pos", "v");
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
			}
			w = w.substring(0, w.length() - 1);
		}
		// can't find possible word in dict, return the input
		return word;
	}
	
}
