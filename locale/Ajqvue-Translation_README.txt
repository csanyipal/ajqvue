Ajqvue Translations

Locale, translations are essentially done through Google Translate,
translate.google.com.

In the project directory is a locale directory. The Ajqvue_en_US.html file is
fed to Google translate. The translation is copied from the browser display then
pasted into the spreadsheet AjqvueBundle_blank.ods column B. The spreadsheet is
then exported in CSV format with UTF-16 encoding. That file is then opened with
JEdit to remove the quotes, commas to become the AjqvueBundle_xx_XX.properties
file. So for example Hungarian, AjqvueBundle_hu_HU.properties.

This is the process used by the project to create the properties files in the
locale directory. It is efficient and allows translation to be performed in
almost any language, but of course relies on Google Translation. Each plugin
must also go through the same process. Around 30 minutes for each project.

JEdit is one of the only text editors that the project has found that allows the
the editing buffer to be set to a specified character encoding. The properties
files MUST BE in UTF-16 ENCODING and saved as such.