# HOUSING PREDICTOR

En la época actual, comprarse una casa llega a ser un lujo que muchos no se pueden permitir, esto es debido a los excesivos precios que algunas personas le ponen a sus casas de manera subjetiva, sin tener en cuenta el valor real que pueda tener la propiedad.

La idea del proyecto es desarrollar una herramienta con la que se pueda contrastar los precios de venta que hay de las propiedades y compararlas con su precio real para así saber si el precio en los portales de venta son justos o no para los compradores.

## APIs y Datamart. 
Para realizar el proyecto decidimos usar dos de los canales de compra de propiedades más grandes que hay a nivel global, Idealista y Fotocasa.

Para Idealista aprovechamos la página RapidAPI donde pudimos encontrar la API de manera gratuita y con gran variedad de información y variables que servirían a posteriori en el proyecto. Fotocasa por otra parte, optamos mejor por el uso de un Scraper, pues la API estaba bloqueada para agentes externos, sin embargo con un Scrapper podíamos conseguir también la información que nos sería de utilidad igualando a lo que podíamos sacar de su API.

El datamart son aquellos datos sobre las propiedades que consideramos de mayor valor para la posterior explotación de estos. Se podrían dividir en varios grupos lo cuáles serían:
- Características físicas de la vivienda:
  - Superficie(metros cuadrados).
  - Nº de habitaciones.
- Características geográfica de la vivienda:
  - Ubicación.
  - Zona o Distrito.
- Precio de la vivienda.

El datamart funciona de manera que recoge la información del dataset original, lo procesa y transforma para después seleccionar las variables e insertarlos en nuestro propio dataset analítico. Se decidió esta manera pues así los datos estarían mejor organizados lo que permitiría la posible expansión del datamart en el futuro.

## Ejecución y compilación.

