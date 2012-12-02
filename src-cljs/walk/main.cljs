(ns walk.main
  (:require [crate.core :as crate]
            [fetch.remotes :as remotes]
            [fetch.core :as core]
            [crate.element :as element]
            [jayq.core :as jq]
            [goog.dom :as dom]
  )
  (:use-macros [crate.def-macros :only [defelem defpartial]])
  (:require-macros [fetch.macros :as fm])
)


(def selectors
  (hash-map
    :map (jq/$ :#map)
    :mapCanvas (jq/$ :#mapCanvas)
    :numbers (jq/$ :#numbers)
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

(defn color-point [p]
  (let [surface (dom/getElement "mapCanvas")
        context (.getContext surface "2d")]
  (set! (. context -fillStyle) "rgb(0,0,255)")
  (.fillRect context (:x p) (:y p) 3 3))
)

(defn display-point [p2]
  (jq/text (jq/$ :#startX) (str (:x (:point p2)) ":" (:y (:point p2))))
  (color-point (:point p2))
)


(defn display-measure [p2]
  (jq/text (jq/$ :#startX) (str (:x (:start (:measure p2))) ":" 
                                (:y (:start (:measure p2)))))
  (jq/text (jq/$ :#measureDistance) (:distance (:measure p2)))
)

(defn color-points [pl]
  (cond 
    (nil? pl) ()
    (nil? (first pl)) ()
    :else 
      (color-points (rest pl) (color-point (first pl)))
  )
)

(defn display-walk [p2]
  (jq/text (jq/$ :#startX) (str (:x (:point p2)) ":" (:y (:point p2))))
  (jq/text (jq/$ :#walkStepDistance) (:distance (:result p2)))
  (jq/text (jq/$ :#walkTotalDistance) (:total-distance (:result p2)))
  (update-error-message "")
  (jq/text (jq/$ :#walkStepUnitDistance) (:segment-unit-distance (:result p2)))
  (jq/text (jq/$ :#walkTotalUnitDistance) (:total-unit-distance (:result p2)))
  (let [pts (:point-list (:result p2))]
    (cond
      (nil? pts) nil
      :else (color-points pts)
    )
  )
)

(defn update-error-message [m]
  (jq/text (jq/$ :#walkError) m)
)

(defn replace-start [p]
  (fm/remote (click-point p) [p2]
  (cond 
    (nil? (:error (:result p2)))
	  (let [state (:state p2) ]
	    (cond 
	      (= state :points) (display-point p2)
	      (= state :measure) (display-measure p2)
	      (= state :walk) (display-walk p2)
	    ))
    :else  (update-error-message (:error(:result p2)))
  )
))


(defn handleClickOnCanvas [e]
  (let  [current-element (js* "this")
         totalOffsetX 0
         totalOffsetY 0
         eventX (. e -pageX)
         eventY (. e -pageY)
         points (getFinalOffset eventX eventY current-element totalOffsetX totalOffsetY) ]
      (replace-start points)
  )
)


;; define the canvas and attach the click event handling function
(defn display-map [map-filename]
  (let [canvas-js (str "var c=document.getElementById(\"mapCanvas\");"
                       "c.style.backgroundImage=\"url(/img/maps/" 
                       map-filename ")\";")]
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


(defn handleModeChange [e] 
  (let [modeValue (. (js* "this") -value)]
    (fm/remote (the-mode modeValue) [p2 ])
  ))

(defn handleUnitChange [e]
  (let [unitValue (. (js* "this") -value)]
    (fm/remote (the-unit-of-measure unitValue) [u2])
  ))

(defn handleScaleDistanceChange [e]
  (let [distance-value (. (js* "this") -value)]
    (js/alert (str "the measure distance is now " distance-value))
    (fm/remote (the-measure-distance distance-value) [u2])
))

(defn display-numbers []
  (replaceHtml (selectors :numbers)
    (crate/html [:p 
                  [:p {:id "modeSelect"} "Mode"
                    [:select {:id "modeSelector"}
                     [:option {:value "points"} "Points"]
                     [:option {:value "measure"} "Measure"]
                     [:option {:value "walk"} "Walk"]
                    ]
                  ]
                  [:table 
                    [:tr [:td "last point clicked"] [:td  [:div#startX] ] ]
                    [:tr [:td "Measured Distance "]
                         [:td [:div#measureDistance]] 
                         [:td "pixels is equal to " ]
                         [:td [:div#inputDistance ]
                              [:input#scaleDistance {:type "text"}]
                         ]
                    ]
                    [:tr [:td {:id "unitsSelect"} "Unit of Measure" ]]
                    [:tr [:td ][:td "Pixels" ]
                         [:td  [:select {:id "unitSelector"}
                                 [:option {:value "miles"} "Miles"]
                                 [:option {:value "kilometers"} "Kilometers"]
                               ]
                         ]
                    ]
                    [:tr [:td "segment distance"] 
                         [:td {:id "walkDistance"} [:div#walkStepDistance] ]
                         [:td [:div#walkStepUnitDistance]]
                    ]
                    [:tr [:td "total distance walkded"]
                        [:td "total distance walked " [:div#walkTotalDistance] ]
                        [:td [:div#walkTotalUnitDistance]]
                   ]
                    [:tr [:td [:div#walkError]]]
                  ]
                ]
    )
  )
  (jq/bind (jq/$ :#modeSelector) :change handleModeChange)
  (jq/bind (jq/$ :#unitSelector) :change handleUnitChange)
  (jq/bind (jq/$ :#scaleDistance) :change handleScaleDistanceChange)

)


(display-numbers)
