#0.2a - 17/01/2019
- HanSoloTSTParser: Fixed intorno values.

#0.2.1a - 24/01/2019
- ParamListHandler: getParamListFormatted() -> Custom value was not used if no default value had been specified.
    setParamElementMin(), setParamElementMax() -> fixed param is set by user to show in main table of Main Controller.
- ParamElement: Added isParamSetByUserForDefaultOrCustom().
- FXMLDocument.fxml: fixed view.

#0.2.2a - 12/04/2019
- FXMLHelpController: fixed help and documentation.
- ParamListHandler: fixed bug with LockedValue = 0 not set after value change.
- Aggiunta la possibilità di realizzare test combinatori per ogni singolo parametro specificato. Inoltre adesso
se non esistono parametri specificati per la funzione non sono generati test intorno sinisto e intorno destro in quanto identici.
- Models Completely Rewritten. Code adapted to new models.