package de.fhg.fokus.odp.entities.model;

import com.liferay.portal.kernel.bean.AutoEscape;
import com.liferay.portal.model.BaseModel;
import com.liferay.portal.model.CacheModel;
import com.liferay.portal.service.ServiceContext;

import com.liferay.portlet.expando.model.ExpandoBridge;

import java.io.Serializable;

import java.util.Date;

/**
 * The base model interface for the MetadataComment service. Represents a row in the &quot;entities_MetadataComment&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * This interface and its corresponding implementation {@link de.fhg.fokus.odp.entities.model.impl.MetadataCommentModelImpl} exist only as a container for the default property accessors generated by ServiceBuilder. Helper methods and all application logic should be put in {@link de.fhg.fokus.odp.entities.model.impl.MetadataCommentImpl}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see MetadataComment
 * @see de.fhg.fokus.odp.entities.model.impl.MetadataCommentImpl
 * @see de.fhg.fokus.odp.entities.model.impl.MetadataCommentModelImpl
 * @generated
 */
public interface MetadataCommentModel extends BaseModel<MetadataComment> {
    /*
     * NOTE FOR DEVELOPERS:
     *
     * Never modify or reference this interface directly. All methods that expect a metadata comment model instance should use the {@link MetadataComment} interface instead.
     */

    /**
     * Returns the primary key of this metadata comment.
     *
     * @return the primary key of this metadata comment
     */
    public long getPrimaryKey();

    /**
     * Sets the primary key of this metadata comment.
     *
     * @param primaryKey the primary key of this metadata comment
     */
    public void setPrimaryKey(long primaryKey);

    /**
     * Returns the uuid of this metadata comment.
     *
     * @return the uuid of this metadata comment
     */
    @AutoEscape
    public String getUuid();

    /**
     * Sets the uuid of this metadata comment.
     *
     * @param uuid the uuid of this metadata comment
     */
    public void setUuid(String uuid);

    /**
     * Returns the _id of this metadata comment.
     *
     * @return the _id of this metadata comment
     */
    public long get_id();

    /**
     * Sets the _id of this metadata comment.
     *
     * @param _id the _id of this metadata comment
     */
    public void set_id(long _id);

    /**
     * Returns the user liferay ID of this metadata comment.
     *
     * @return the user liferay ID of this metadata comment
     */
    public long getUserLiferayId();

    /**
     * Sets the user liferay ID of this metadata comment.
     *
     * @param userLiferayId the user liferay ID of this metadata comment
     */
    public void setUserLiferayId(long userLiferayId);

    /**
     * Returns the metadata name of this metadata comment.
     *
     * @return the metadata name of this metadata comment
     */
    @AutoEscape
    public String getMetadataName();

    /**
     * Sets the metadata name of this metadata comment.
     *
     * @param metadataName the metadata name of this metadata comment
     */
    public void setMetadataName(String metadataName);

    /**
     * Returns the text of this metadata comment.
     *
     * @return the text of this metadata comment
     */
    @AutoEscape
    public String getText();

    /**
     * Sets the text of this metadata comment.
     *
     * @param text the text of this metadata comment
     */
    public void setText(String text);

    /**
     * Returns the created of this metadata comment.
     *
     * @return the created of this metadata comment
     */
    public Date getCreated();

    /**
     * Sets the created of this metadata comment.
     *
     * @param created the created of this metadata comment
     */
    public void setCreated(Date created);

    @Override
    public boolean isNew();

    @Override
    public void setNew(boolean n);

    @Override
    public boolean isCachedModel();

    @Override
    public void setCachedModel(boolean cachedModel);

    @Override
    public boolean isEscapedModel();

    @Override
    public Serializable getPrimaryKeyObj();

    @Override
    public void setPrimaryKeyObj(Serializable primaryKeyObj);

    @Override
    public ExpandoBridge getExpandoBridge();

    @Override
    public void setExpandoBridgeAttributes(BaseModel<?> baseModel);

    @Override
    public void setExpandoBridgeAttributes(ExpandoBridge expandoBridge);

    @Override
    public void setExpandoBridgeAttributes(ServiceContext serviceContext);

    @Override
    public Object clone();

    @Override
    public int compareTo(MetadataComment metadataComment);

    @Override
    public int hashCode();

    @Override
    public CacheModel<MetadataComment> toCacheModel();

    @Override
    public MetadataComment toEscapedModel();

    @Override
    public MetadataComment toUnescapedModel();

    @Override
    public String toString();

    @Override
    public String toXmlString();
}
