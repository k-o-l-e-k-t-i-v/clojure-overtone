;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone

(def root-cbus (control-bus))

(defsynth root-metro [c-bus 10 rate 32 count 40]
  (let [trig (impulse:kr rate)
        count (stepper:kr trig :min 1 :max count)]
    (send-trig:kr trig count)
    (out:kr root-cbus trig)))

(def root-metro (root-metro))

(definst kik [freq 30 div 1 bus 0 dec 0.1]
  (let [t   (pulse-divider:kr (in:kr bus) div)
        e   (decay t dec)
        fe  (+ freq (* 500 (decay t (* 0.3 dec))))
        s   (sin-osc fe)
        sig (* e s)]
     (out [0 1] sig)))

(def t2 (kik :bus root-cbus :div 10))
(def t2 (kik :bus root-cbus :div 8 :freq 50))
(def t3 (kik :bus root-cbus :div 25 :freq (midi->hz (note :d#2))))
(ctl t1 :div 12 )
(ctl t1 :dec 0.04)
(ctl t2 :div 25)
(ctl t1 :freq (midi->hz (note :d5)))
(ctl t1 :dec 0.8)
(remove-event-handler ::root-metro)

;(defn change-temp [])
(let [divider [12 18 3 6 4 14 9 2 7 10 17 19 23]
      chords [:c1 :d1 :a#1 :g1]
      t [t1 t2 t3]]
  (do
    (map #(ctl % :div (choose divider) :freq (midi->hz (note (choose (chord (choose chords) (choose [:11 :7 :minor]) (rand-int 2)))))) t)))
(let [d [2 6 8]
      t [t1 t2 t3]
      f [400 300 950]]
  (map #(ctl %2 :div %1 :freq %3) d t f))



(change-temp )
;;(clip)
(defn change-root []
  (ctl root-metro :rate (+ 4 (* 4 (rand 8))) ))
(def metro (metronome 120))
(defn ch [beat t]
  (at (metro beat) (change-root))
  (apply-by (metro (+ t beat)) ch (+ t beat) t []))

(ch (metro) 4)
(metro :bpm 320)

(on-event "/tr" (change-temp) ::root-metro)
(stop)
