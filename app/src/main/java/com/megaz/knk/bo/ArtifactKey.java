package com.megaz.knk.bo;

import com.megaz.knk.constant.ArtifactPositionEnum;
import com.megaz.knk.entity.ArtifactDex;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class ArtifactKey {
    String setId;
    ArtifactPositionEnum position;

    public ArtifactKey(String setId, ArtifactPositionEnum position) {
        this.setId = setId;
        this.position = position;
    }

    public static ArtifactKey getArtifactKey(ArtifactDex artifactData) {
        return new ArtifactKey(artifactData.getSetId(), artifactData.getPosition());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtifactKey that = (ArtifactKey) o;
        return Objects.equals(setId, that.setId) && position == that.position;
    }

    @Override
    public int hashCode() {
        return Objects.hash(setId, position);
    }
}
