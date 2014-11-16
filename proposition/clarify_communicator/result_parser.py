__author__ = 'Srinivas Prasad Gumdelli'
"""
 module: result_parser
 synopsis: parses the results returned by the communicator
"""
import json
from clarify_communicator import communicator
import numpy.random

class ResultParser:
    def __init__(self, api_key):
        self.communicator_instance = communicator.ClarifyMessenger(api_key)

    def _get_bundle_list(self, meta_data):
        """

        :param meta_data:
        :return:
        """
        items = self.communicator_instance.search_with_meta(meta_data=meta_data)
        return items["_links"]["items"]

    def _get_track_path(self, bundle_path):
        bundle_info = self.communicator_instance.retrieve_bundle_data(bundle_path=bundle_path)
        return json.loads(bundle_info.json)["_links"]["clarify:tracks"]["href"]

    def get_random_track_url(self, meta_data):
        bundle_list = self._get_bundle_list(meta_data)

        if len(bundle_list) == 0:
            return "error"

        else:
            random_val = numpy.random.randint(0, len(bundle_list))
            random_bundle_path = bundle_list[random_val]["href"]

            track_path = self._get_track_path(random_bundle_path)

            track_url_json = self.communicator_instance.retrieve_track_data(track_path=track_path)

            track_url = json.loads(track_url_json.json)["tracks"][0]["media_url"]

            return track_url


