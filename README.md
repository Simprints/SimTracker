# SimTracker

A fork of the [DHIS2 Android app](https://github.com/dhis2/dhis2-android-capture-app).

## Differences from the upstream

The features below may be in a prototype stage.

### Loading ProjectID & ModuleID dynamic (custom) attributes to use in TEI (beneficiary) profiles

SimTracker is intended to connect to Simprints ID, so the data models shared between the two apps are conceptually related, approximately the following way:

| Concept \ App                                   | SimTracker (DHIS2) | Simprints ID |
|-------------------------------------------------|--------------------|--------------|
| A healthcare solution beneficiaries enroll into | Program            | Project      |
| Geographical area or a healthcare facility      | Organisation Unit  | Module       |

#### Support for a `ProjectID` text input field as a dynamic attribute of a Program

* In a DHIS2 instance admin web UI, `ProjectID` can be added though `Menu` -> `Maintenance` -> `Other` -> `Attribute`: add a text-value attribute named `ProjectID`, make it apply to the `Program` object.
* To edit or verify that the `ProjectID` custom attribute is available in Programs, see `Menu` -> `Maintenance` -> `Program` -> `Program`: the `ProjectID` input field should be visible for existing or new Programs.
* SimTracker, connected to the same DHIS2 instance, will download and internally store the up-to-date `ProgramUid`-to-`ProjectID` key-value mappings when the sync is performed, for the Programs where the `ProjectID` dynamic attribute value is defined (isn't blank).
* When viewing a profile of a TEI enrolled into a Program for which a `ProjectID` is defined, the value of the `ProjectID` will be displayed.

#### Support for a `ModuleID` text input field as a dynamic attribute of an Organisation Unit

* In a DHIS2 instance admin web UI, `ModuleID` can be added though `Menu` -> `Maintenance` -> `Other` -> `Attribute`: add a text-value attribute named `ModuleID`, make it apply to the `Organisation unit` object.
* To edit or verify that the `ModuleID` custom attribute is available in Organisation Units, see `Menu` -> `Maintenance` -> `Organisation unit` -> `Organisation unit`: the `ModuleID` input field should be visible for existing or new Organisation Units.
* SimTracker, connected to the same DHIS2 instance, will download and internally store the up-to-date `OrganisationUnitUid`-to-`ModuleID` key-value mappings when the sync is performed, for the Organisation Units where the `ModuleID` dynamic attribute value is defined (isn't blank).
* When viewing a profile of a TEI belonging to an Organisation Unit for which a `ModuleID` is defined, the value of the `ModuleID` will be displayed.
* If `ModuleID` isn't defined for the TEI's exact Organisation Unit, but is defined for an upper level Organisation Unit, the upper level one's `ModuleID` will be used instead. If Organisation Units on multiple upper levels have `ModuleID`, the lowest level one will be used.

### Automated fork sync with the upstream

A [sync-from-upstream](https://github.com/Simprints/SimTracker/blob/main/.github/workflows/sync-from-upstream.yml) GitHub Action periodically creates a pull request to merge the changes in the upstream to this fork.