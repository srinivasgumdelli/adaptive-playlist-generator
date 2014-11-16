__author__ = 'Srinivas Prasad Gumdelli'
"""
 module: communicator
 synopsis: Communicates with the Clarify (http://clarify.io) API
"""

from clarify_python_2 import clarify

class ClarifyMessenger:

    def __init__(self, api_key):
        clarify.set_key(api_key)

    def create_bundle(self, music_file_url):
        """
        This method is currently not being implemented, being held off for further extension

        :param music_file_url: url of the music file, currently not designed, use Amazon S3 buckets may be?
        :return: response json from the clarify create
        """
        pass

    def search_with_meta(self, meta_data):
        """
        This method sends a request to the Clarify API to search for the bundles that contain the given meta_data

        :param meta_data: the meta data that we would like to search with
        :return: json of results that match the given meta_data, using the Clarify API
        """
        results_json = {}

        try:
            results_json = clarify.search(query_fields="metadata.*", query=meta_data)
        except clarify.APIException, err:
            print err.get_message()

        return results_json

    def retrieve_bundle_data(self, bundle_path):
        """
        This method retrieves the bundle data for the given path

        :param path: the path of the bundle
        :return: the information about the bundle in json format
        """

        result_json = {}

        try:
            result_json = clarify.get(path=bundle_path)
        except clarify.APIException, err:
            print err.get_message()

        return result_json

    def retrieve_track_data(self, track_path):
        """
        This method retrieves the track data for the given bundle

        :param track_path:
        :return: the information about the track in json format
        """
        result_json = {}

        try:
            result_json = clarify.get(path=track_path)
        except clarify.APIException, err:
            print err.get_message()

        return result_json
