(ns walk.main
  (:require [crate.core :as crate]
            [crate.element :as element]
            [crate.util :as cr_util]
;            [fetch.remotes :as remotes]
;            [fetch.core :as core]
            [jayq.core :as jq]
            [goog.events :as events]
            [goog.events.EventType :as event-type]
  )
  (:use-macros [crate.def-macros :only [defelem defpartial]])
  (:require-macros [fetch.macros :as fm])
)


(def selectors
  (hash-map
    :map (jq/$ :#map)
    :mapCanvas (jq/$ :#mapCanvas)
))

(defn replaceHtml [selector new-text]
  (jq/append selector new-text))

;; implementation of algorithm from 
;; http://stackoverflow.com/questions/55677/how-do-i-get-the-coordinates-of-a-mouse-click-on-a-canvas-element
;; most accepted answer
(defn getFinalOffset [eventX eventY current-element totalOffsetX totalOffsetY]
  (cond 
    (nil? current-element) {:x (- eventX totalOffsetX) :y (- eventY totalOffsetY)}
    (= 3 level) {:x totalOffsetX :y totalOffsetY}
    :else (getFinalOffset eventX eventY (. current-element -offsetParent) (+ totalOffsetX (. current-element -offsetLeft)) (+ totalOffsetY (. current-element -offsetTop)))
  )
)

(defn handleClickOnCanvas [e]
  (let  [current-element (js* "this")
         totalOffsetX 0
         totalOffsetY 0
         eventX (. e -pageX)
         eventY (. e -pageY)
        ]
    
    (let [points (getFinalOffset eventX eventY current-element totalOffsetX totalOffsetY) ]
      (js/alert (str "calculated x " (:x points) " and y " (:y points)))
   )
  )
)


;; define the canvas and attach the click event handling function
(defn display-map [map-filename]
  (let [canvas-js (str "var c=document.getElementById(\"mapCanvas\");"
                      "c.style.backgroundImage=\"url(/img/maps/" map-filename ")\";")]
  (replaceHtml (selectors :map)
    (crate/html [:p 
                    [:canvas {:id "mapCanvas" 
                          :width 640 
                          :height 400 
                          :tabindex 1
                          :style "border:1px solid #000000"}]
                  (element/javascript-tag canvas-js)
                ]
    ))
  )
  (jq/bind (jq/$ :#mapCanvas) :click handleClickOnCanvas)
)

(display-map "testMap.png")
