__author__ = 'Srinivas Prasad Gumdelli'
"""
 module: song_proposition
 synopsis: pulls up a song url based on genre (suggested according to activity level)
"""

from clarify_communicator import result_parser
import os

class Proposition:

    def get_url_suggestion(self, heart_beat):
        rp = result_parser.ResultParser(os.environ['CLARIFY_API_KEY'])
        track_url = ""
        if 120 <  heart_beat <137:
            track_url = rp.get_random_track_url(meta_data="fast")
        elif 137 < heart_beat < 148:
            track_url = rp.get_random_track_url(meta_data="sample")
        elif 148 < heart_beat < 160:
            track_url = rp.get_random_track_url(meta_data="slow")

        return track_url
