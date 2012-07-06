(ns walk.main
  (:require [crate.core :as crate]
            [crate.element :as element]
;            [fetch.remotes :as remotes]
;            [fetch.core :as core]
            [jayq.core :as jq]
  )
  (:use-macros [crate.def-macros :only [defelem defpartial]])
  (:require-macros [fetch.macros :as fm])
)


(def selectors
  (hash-map
    :map (jq/$ :#map)
))

(defpartial map-partial [map-filename]
  (element/image (str "/img/maps/" map-filename) "Map"))

(defn replaceHtml [selector new-text]
  (jq/append selector new-text))

(defn display-map [map-filename]
  (replaceHtml (selectors :map)
    (crate/html [:p (map-partial map-filename)]))
)

(display-map "testMap.png")
