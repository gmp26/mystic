(ns ^:figwheel-always mystic.cards

  (:require
   [rum.core :as rum]
   [mystic.core :as core]
   [mystic.components :as comp])

  (:require-macros
   [devcards.core :as dc :refer [defcard deftest]]
   ))

(enable-console-print!)

(defcard basic-mystic-rose
  (comp/basic-mystic-rose core/model))

(defcard four-draw-methods
  (comp/side-by-side core/model))

#_(defcard stars
  (comp/stars core/model 2))
