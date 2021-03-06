@Requires{Desugaring: resets}
module lang::missgrant::base::Compile

import lang::missgrant::base::AST;

str compile(str name, Controller ctl) =
       "public class <name> {
       '  public static void main(String args[]) throws java.io.IOException { 
       '     new <name>().run(new java.util.Scanner(System.in), 
       '                    new java.io.PrintWriter(System.out));
       '  }
       '  <states2consts(ctl.states)>
       '  <controller2run(ctl)>
       '  <for (e <- ctl.events) {>
  	     '    <event2java(e)>
  	     '  <}>
       '  <for (c <- ctl.commands) {>
       '  <command2java(c)>
       '  <}>
       '}";

str states2consts(list[State] states) {
  i = 0;
  return "<for (s <- states) {>
         'private static final int <s.name> = <i>;
         '<i += 1;}>"; 
}


// Needed to get Java/JDT compatible ids for 
// synthesized names.
str output() = "output";
str token() = "token";
str current() = "current";

str command2java(Command command) =
         "private void <command.name>(java.io.Writer <output()>) throws java.io.IOException {
         '  <output()>.write(\"<command.token>\\n\");
         '  <output()>.flush();
         '}";


str event2java(Event event) =
         "private boolean <event.name>(String <token()>) {
         '  return <token()>.equals(\"<event.token>\");
         '}";


str controller2run(Controller ctl) =
         "void run(java.util.Scanner input, java.io.Writer output) throws java.io.IOException {
         '  int <current()> = <ctl.states[0].name>;
         '  while (true) {
         '    String <token()> = input.nextLine();
         '    <for (s <- ctl.states) {>
         '    <state2case(s)>
         '    <}>
         '  }
         '}";

str state2case(State s) =
         "if (<current()> == <s.name>) {
         '  <for (a <- s.actions) {>
         '     <a>(<output()>);
         '  <}>
         '  <for (transition(e, s2) <- s.transitions) {>
         '  if (<e>(<token()>)) {
         '     <current()> = <s2>;
         '  }
         '  <}>
         '  continue;
         '}";

