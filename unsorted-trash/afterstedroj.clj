;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone





(def hat-pattern  [[:bd 1]
                   [:sd 2]
                   [:hh 1/4]
                   [:hh 1/4]
                   [:bd 1]
                   [:rest 1/2]
                   [:sd 1]
                   [:hh 1/2]
                   [:rest 1/2]
                   [:hh 1/2]
                   [:sd 1]
                   [:hh 1/2]
                   [:bd 1/2]
                   [:rest 1/2]])

(def kick-pattern [[:bd 1]
                   [:bd 2]
                   [:bd 1]
                   [:bd 2]
                   [:bd 2]
                   [:bd 6]])



(print base-pattern)

(definst kick []
  (let [freq-env     (env-gen (perc 0 0.02 500 -10) 1 1 40 1)
        freq-env-2   (env-gen (perc 0 0.02 1500 -10) 1 1 60 1)
        amp-env      (env-gen (perc 0 0.2 1 -5) 1 1 0 1)
        source-sig   (mix [(lf-noise2:ar freq-env-2) (sin-osc freq-env) (sin-osc-fb freq-env-2 0.5)])
        drive        (distort (* (* 0.2 freq-env) source-sig))
        amp-sig      (* amp-env drive)
        source-sig   (* amp-env source-sig)
        room         (freq-shift:ar (free-verb source-sig 0.993 0.294 0.39) -100)
        mix-it       (mix [source-sig (* 0.53 amp-sig) room])
        signal-out   (pan2 mix-it 0 1)
        silence      (detect-silence signal-out 0.001 0.3 FREE)]
    (out 0 signal-out)))
(kick)

(definst snare []
  (let [freq-env     (env-gen (perc 0 0.2 1000 -10) 1 1 20 1)
        freq-env-2   (env-gen (perc 0 0.2 500 -10) 1 1 0 1)
        amp-env      (env-gen (perc 0 0.2 1 -5) 1 1 0 1)
        source-sig   (mix [(bpf (white-noise) 1300 4) (sin-osc-fb freq-env 0.6) (sin-osc-fb freq-env-2 0.95)])
        drive        (distort (* (* 2 freq-env) source-sig))
        amp-sig      (* amp-env drive)
        source-sig   (* amp-env source-sig)
        room         (freq-shift:ar (free-verb source-sig 0.993 0.294 0.39) -100)
        mix-it       (mix [source-sig (* 0.53 amp-sig) room])
        signal-out   (pan2 mix-it 0 1)
        silence      (detect-silence signal-out 0.001 0.3 FREE)]
    (out 0 signal-out)))
(snare)

(definst hat []
  (let [freq-env     (env-gen (perc 0 0.2 500 -10) 1 1 40 1)
        freq-env-2   (env-gen (perc 0 0.2 1500 -10) 1 1 60 1)
        amp-env      (env-gen (perc 0 0.2 1 -5) 1 1 0 1)
        source-sig   (mix [(white-noise) (lf-noise2:ar freq-env) (lf-noise2:ar freq-env-2)])
        drive        (distort (* (* 0.2 freq-env) source-sig))
        amp-sig      (* amp-env drive)
        source-sig   (* amp-env source-sig)
        room         (freq-shift:ar (free-verb source-sig 0.993 0.294 0.39) -100)
        mix-it       (mix [source-sig (* 0.53 amp-sig) room])
        mix-it       (rhpf mix-it 1000 0.5)
        signal-out   (pan2 mix-it 0 1)
        silence      (detect-silence signal-out 0.001 0.3 FREE)]
    (out 0 signal-out)))
(hat)

(def time-unit 25)


;(dela)

(defn pick-inst [k]
  (cond (= k :bd) (kick)
        (= k :sd) (snare)
        (= k :hh) (hat)))

(defn pattern-player
  [pattern time-unit ted]
  (let [prvni (first pattern)
        co (get prvni 0)
        kdy (+ ted (* time-unit (get prvni 1)))
        otoc (rotate 1 pattern)]
    (at ted (pick-inst co))
    (apply-at kdy pattern-player otoc time-unit kdy [])
    ))


(pattern-player base-pattern 200 (now))

(pattern-player hat-pattern 200 (now))
(pattern-player kick-pattern 200 (now))


(def m1 (metronome 80))
(metro-bpm m1 40)

(apply-at (m1 (m1)) (pattern-player kick-pattern 200 (now)))
(apply-at (m1 (m1)) (pattern-player hat-pattern 200 (now)))


(kill 867)


(stop)
(apply-a])

;(pattern-player base-pattern 40)
