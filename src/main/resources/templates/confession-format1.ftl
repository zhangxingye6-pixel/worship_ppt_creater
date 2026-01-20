<#-- 每一节前面带着像章号和节号的信条， 如1.2 所谓「圣经」，或说「记载下来的圣言」…………-->
<#list confessionVerseList as item>[${item.chapter}.${item.verse}] ${item.content}<#sep>${"\n"}</#list>