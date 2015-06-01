;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone
(ns overtone.midi.test.novation
  (:use [overtone.core]
        [overtone.synth.sts :only [prophet]]
        [overtone.synth.ixi]))


(stop)

(definst gag
  [gat  1
   amp  1
   freq 200]
  (let  [g (env-gen (adsr 2 0 1 3 amp -10 0 ) gat 1 0 1 FREE)
         x #(* (pow (lf-noise2:ar %2) 3) (square %1))
         cl (into [] (repeatedly 10 #(rand 100)))
         al (into [] (repeatedly 10 #(rand 8)))
         s (map x (map #(+ freq %) cl) al)
         f (lin-exp (sin-osc (+ 5 (* 3 (lf-noise2:ar (+ 20 (rand 8)))))) -1 1 40 2000)
         f2 (lin-exp (sin-osc (+ 10 (* 9 (lf-noise2:ar (+ 2 (rand 8)))))) -1 1 0.15 1)
         s (apply + (map #(resonz % f f2) s))
         s (normalizer s)
         s (free-verb s 0.9 0.9 0.2)
         s (* g s)]
    (out 0 (pan2 s))))


(stop-all)

(def g1 (gag :freq 90))
(def g2 (gag :freq 200))

(ctl g1 :gat 0)
(ctl g2 :gat 0)
(ctl gag :gat 0)

(stop)

(demo 100 (let [i (in:ar 0 1)
                i2 (in:ar 1 1)
                r1 (free-verb i 0.9 0.8 0.2)
                r2 (free-verb i2 0.49 0.98 0.2)
                ]
            (replace-out:ar 0 r1)
            (replace-out:ar 1 r2)))

(node-tree)

(server-status)

(println (midi-connected-devices))

(odoc midi-device-num)
(midi-device-keys)
(odoc midi-device-keys)
(odoc midi-find-connected-devices)

(midi-find-connected-devices :name)
(source midi-connected-devices)

(midi-find-connected-device "SL")
(def zro (midi-find-connected-device "SL"))
(midi-mk-full-device-key zro)

(on-event (conj (midi-mk-full-device-key zro) :note-on)
          (fn [m]
            (println m)
            (let [note (:note m)]
              (prophet :freq (midi->hz note)
                       :decay 5
                       :rq 0.6
                       :cutoff-freq 1000)))
          ::prophet-midi)
(stop)

(kick)
(kick2)
(kick3)
(snare)
(stop)

;(clojure.)
;(so kick2)

;(doc kick2)
