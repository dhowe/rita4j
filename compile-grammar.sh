GRAMMAR_DIR=../rita2js/grammar
OUTPUT_DIR=src/main/java/rita/grammar

java -Xmx500M -cp 'lib/antlr-4.7.1-complete.jar:$CLASSPATH' org.antlr.v4.Tool -Dlanguage=Java -lib $GRAMMAR_DIR -o .grammar -visitor -Xexact-output-dir -package rita.grammar $GRAMMAR_DIR/RiScript.g4
cp .grammar/*.java $OUTPUT_DIR
ls $OUTPUT_DIR