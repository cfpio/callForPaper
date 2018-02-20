# User Guide

This is a work-in-progress user-guide, if something looks weird, feel free to contribute :P

## Get an account

Send us an email at [info@cfp.io](mailto:info@cfp.io) to request an instance. We just need your event name, typically give us event URL
or Twitter account so we can use this commonly used name as ID for your instance. Your URL will be `https://**YourEvent**.cfp.io`.

## Configure your call for papers

Login as owner (the one who requested the CFP instance) and select "Configuration" in menu. This page lets you

- define the event name, date, duration in days, logo (used when sending notification emails) and contact email.
- define the administrators, who will be able to review proposals, vote and select then to establish event's schedule
- define the reviewer - this feature is a work-in-progress, no distinction with administrators at this time
- define themes, formats, and rooms. Themes (aka "tracks") are the various topics covered by your event. Formats are the various session types and durations (conference, workshop, ...). Rooms is a description of the event's venue, used for event scheduling.

When everything looks good, you can enable submission

## Collect proposals

Up to you to share CFP link on social medias so speakers are aware your CFP is open. During last days before the closing date,
it might be useful to reming people your CFP is open for x days.

Potential speakers will access `https://**YourEvent**.cfp.io` to setup their profile and send you proposals.
As an administrator, you can access the review menu to see current status of your CFP: some statistics and the list of submitted proposals.
On each proposal you can start a review. The review lets you post questions / comments to the speaker if needed, or add some private notes
for internal review. As a result you rate each proposal between 1 (dislike) and 5 (love), or just log you don't have opinion on it
(maybe you have no idea what this talk is about, but others in the team will).

Don't wait the last minute to start reviewing proposals. The earlier you ask a speaker for more details, more chance you have to get a good
evaluation of his content.

## Select proposals

When the CFP is closed and members of the team have reviewed all proposals, it's time to select your event content.

You can use the proposal list and use the "Accept/Reject" buttons to just select talks based on rating. This button changes the state but
dos not send any email to speaker, so it's safe to change your mind.

You can also export the proposals to real life paper format so you can easily debate with your teamates.

When you have established the list of approved proposals, select the "scheduling" tab and use "reject others" to enforce all proposals to have
state set between approved vs rejected. You can select some proposals as backup if you worry some speaker might not come and you need a
replacement talk.

The notification link do send CFP status emails to speakers. At this time this will be too late to change your mind, or at least you'll have
to send a kind personnal email to speakers.

If you have configured rooms and event duration, you can rely on the drag and drop scheduler available on your CFP as `/scheduler.html`.
This one let you organize your event with all proposed talks directly on an agenda grid. Data can then be exported in
[fullcalendar](https://fullcalendar.io/) or [sched.com](https://sched.com/) formats - if you need any other format, just let us know, this is
very easy to add extra formats.

## Missing something ?

Ask us!
Review existing [issues / feature requests](https://github.com/cfpio/callForPapers/issues) or log a new one.
contribute, we welcome pull-requests :)

PLEASE NOTE : the frontend webUI is a legacy one, we maintain it until fresh new UI is ready for production.
So if you have in mind to fully rework the UI, please ping us so we can sync and maybe use your work as basis for new UI, at least
for a submodule.
