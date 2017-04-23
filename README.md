# batch-lemmatize-processor
Dedicated tool for file-batch lemmatization using MorphoDiTa with only one output

Tento nástroj slouží pro dávkové volání morphodity.

Po stáhnutí spustitelného souboru run_wrapper.1.0.jar se soubor spustí takto z příkazové řádky

java -jar run_wrapper.jar run_tagger.exe(morphodita tools) datamodel.tagger(morphodita data model) inputFolder [outputFile]


kde:
run_tagger.exe je nástroj MorphoDiTy
datamodel.tagger je datový model pro MorphoDiTu
inputFolder je cestou ke složce, kde jsou soubory ve formátu txt připravené ke zpracování
[outputFile] je nepovinný parametr. Pokud je uveden, výsledky budou uloženy do něho. Jinak je vytvořen nový soubor v aktuálním adresáři.

V průběhu programu jsou vytvořeny tyto příkazy:
run_tagger data_model inputFile1:outputFile1 inputFile2:outputFile2 ..... až do délky 7000 znaků (8192 znaků je pro Windows CMD limitující) a tyto příkazy jsou spuštěny paralelně.
Optimálně je tak využito prostředků PC a je minimalizováno inicializování taggeru na nutné minimum.


Výstupem tohoto jednoúčelového programu je jeden výstup ve formátu CSV