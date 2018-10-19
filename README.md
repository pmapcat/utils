# utils

* TODO: add docstring with Example metadata for every function you want to export. 
* Fork Codox and make it output data in the markdown format. (Basically, readme generator)

A collection of small useful Clojure functions

## Usage

The library has the following sub directories

### Scales

**Scale** Will calculate linear scaling (mapping)
from one metric into another

```clojure
(require '[thereisnodot.utils.scale :as scale])
(map (partial scale/scale 1 5 10 50)  (range 1 6)) => (list 10 20 30 40 50)
```

**Log Scale** Will calculate log scaling (mapping)
from one metric into another
```clojure
(require '[thereisnodot.utils.scale :as scale])

(map int (map (partial scale/log-scale 1 5 10 50)  (range 1 6)))
=>  (list 10 14 22 33 49)
```

**Log Scale Round**  Will round log scale

```clojure
(require '[thereisnodot.utils.scale :as scale])
(map (partial scale/log-scale-round 1 5 10 50)  (range 1 6)) => b(10.0 14.0 22.0 33.0 49.0)
```

**Haversine**  Will calculate Haversine distance between two points on a shphere

```clojure
(require '[thereisnodot.utils.scale :as scale])
(sca)

```



### Collections
### Spreadsheets
### Markdown
### CSS

### Minor stuff

#### Strings
#### FS
#### Framerate
#### Transliterate
#### UUID
#### URLS
#### HTML

## License

Copyright © 2018 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
