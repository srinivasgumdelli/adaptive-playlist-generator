import os
from clarify_communicator import result_parser

from song_proposition import proposition

print proposition.Proposition().get_url_suggestion(139)
#
# test = communicator.ClarifyMessenger(os.environ['CLARIFY_API_KEY'])
# # print test.search_with_meta("sample")
# # bundle_path = "/v1/bundles/7644c3b59d8c4a1195e5b34b8bf13fa6"
# bundle_path = "/v1/bundles/78680d76d129417183f54a6e28edbe8d/tracks"
# print test.retrieve_bundle_data(bundle_path)


# test = result_parser.ResultParser(os.environ['CLARIFY_API_KEY'])
# # test.get_bundle_list(meta_data="")
#
#
# test = result_parser.ResultParser(os.environ['CLARIFY_API_KEY'])
# # test.get_bundle_list(meta_data="")
#
#
# test = result_parser.ResultParser(os.environ['CLARIFY_API_KEY'])
# # test.get_bundle_list(meta_data="")
# test = result_parser.ResultParser(os.environ['CLARIFY_API_KEY'])
# # test.get_bundle_list(meta_data="")
# test = result_parser.ResultParser(os.environ['CLARIFY_API_KEY'])
# # test.get_bundle_list(meta_data="")


# test = result_parser.ResultParser(os.environ['CLARIFY_API_KEY'])
# print test.get_random_track_url("sample")

# bundle_path = "/v1/bundles/7644c3b59d8c4a1195e5b34b8bf13fa6"
# print test._get_track_href(bundle_path=bundle_path)
# print test.get_random_track_url("sample")