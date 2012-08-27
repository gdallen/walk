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

(defn replaceHtml [selector new-text]
  (jq/append selector new-text))

(defn display-map [map-filename]
  (let [canvas-js (str "var c=document.getElementById(\"mapCanvas\");"
                      "c.style.backgroundImage=\"url(/img/maps/" map-filename ")\";"
                  )]
  (replaceHtml (selectors :map)
    (crate/html [:p 
                  [:canvas {:id "mapCanvas" 
                          :width 640 
                          :height 400 
                          :style "border:1px solid #000000"}]
                  (element/javascript-tag canvas-js)
                ]))
  )
)

(display-map "testMap.png")
